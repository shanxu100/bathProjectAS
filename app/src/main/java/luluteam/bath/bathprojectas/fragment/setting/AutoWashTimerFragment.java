package luluteam.bath.bathprojectas.fragment.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.databinding.FragmentAutoWashTimerBinding;
import luluteam.bath.bathprojectas.fragment.main.ManageSetFragment;
import luluteam.bath.bathprojectas.model.AutoWashTimerInfo;
import luluteam.bath.bathprojectas.model.binding.AutoWashTimerBinding;
import luluteam.bath.bathprojectas.utils.DatePickDialogUtil;
import luluteam.bath.bathprojectas.utils.DensityUtil;
import luluteam.bath.bathprojectas.utils.TimeUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;
import luluteam.bath.bathprojectas.view.dialog.datePickDialog.DatePick;

@Deprecated
public class AutoWashTimerFragment extends Fragment implements ManageSetFragment.AutoWashTimerCallback {
    public static final String TAG = "AutoWashTimerFragment";
    private static final String[] toiletItem = {"男厕", "女厕", "残卫"};
    private static Handler initRequestHandler = null;

    private FragmentAutoWashTimerBinding mBinding;
    private AutoWashTimerBinding currentBinding;
    private ConcurrentHashMap<Character, AutoWashTimerBinding> timerInfoMap = new ConcurrentHashMap<>();
    private String lastToiletId = "";

    private AutoWashTimerInfo autoWashTimerInfo = new AutoWashTimerInfo();
    private AutoWashTimerInfo.Item currentItem = autoWashTimerInfo.new Item();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_auto_wash_timer, container, false);
        View view = mBinding.getRoot();
        this.currentBinding = new AutoWashTimerBinding(APPConstant.UsageMap.get("男厕").charAt(0),
                'X', "00:00:00", "00:00:00", "60", "15", "0");
        mBinding.setCurrentBinding(currentBinding);
        initView();
        setChooseTimer();
        initListeners();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!lastToiletId.equals(APPConstant.TOILETID)) {
            //自动请求两次，没办法的事。因为下位机处理性能有限，有时候会不响应
            doRefresh(12000);
            doRefresh(12000);
            lastToiletId = APPConstant.TOILETID;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (initRequestHandler != null) {
            initRequestHandler.removeCallbacksAndMessages(null);
            initRequestHandler = null;
        }
    }

    private void changeItem(char usage) {
        if (autoWashTimerInfo != null) {
            switch (usage) {
                case '1':
                    currentItem.deepCopy(autoWashTimerInfo.getManItem());
                    break;
                case '2':
                    currentItem.deepCopy(autoWashTimerInfo.getWomanItem());
                    break;
                case '3':
                    currentItem.deepCopy(autoWashTimerInfo.getDisableItem());
                    break;
                default:
                    currentItem.deepCopy(autoWashTimerInfo.getManItem());
            }
        }
    }

    /**
     * 每次切换厕所，将绑定的值写入到info中
     *
     * @param usage
     */
    private void updateInfo(char usage) {
        switch (usage) {
            case '1':
                binding2Item(autoWashTimerInfo.getManItem());
                break;
            case '2':
                binding2Item(autoWashTimerInfo.getWomanItem());
                break;
            case '3':
                binding2Item(autoWashTimerInfo.getDisableItem());
                break;
            default:
                binding2Item(autoWashTimerInfo.getManItem());
        }
    }

    private void binding2Item(AutoWashTimerInfo.Item item) {
        String startTime = "000000", endTime = "004000";
        try {
            startTime = TimeUtil.getTime(currentBinding.getStartTime(), "HH:mm:ss", "HHmmss");
            endTime = TimeUtil.getTime(currentBinding.getEndTime(), "HH:mm:ss", "HHmmss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        item.startTime = startTime;
        item.endTime = endTime;
        item.voiceInterval = currentBinding.getVoiceInterval();
        item.voiceLength = currentBinding.getVoiceLength();
        item.boosterPumpDelayTime = currentBinding.getBoosterPumpDelayTime();
    }

    private void initView() {
        /**
         * 初始化Usage的Spinner的value
         *
         */
        List<String> list = Arrays.asList(toiletItem);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.timerSpinner.setAdapter(adapter);
        mBinding.timerSpinner.setDropDownVerticalOffset(DensityUtil.dip2px(getContext(), 20));
    }

    private void setChooseTimer() {
        DatePickDialogUtil.setEditTextFocus(getContext(), mBinding.autoWashTimerStartTimeEt, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), mBinding.autoWashTimerEndTimeEt, DatePick.TYPE_ONLYTIME);
    }

    private void initListeners() {
        mBinding.refreshAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRefresh();
            }
        });
        mBinding.autoWashTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(getContext());
                loadingDialog.showWithTimeout(3000);
                doCommit();
                doCommit();
                doRefresh(3000);
            }
        });
        mBinding.timerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("mBinding.timerSpinner.getSelectedItem()=====" + mBinding.timerSpinner.getSelectedItem());
                char selectedUsage = APPConstant.UsageMap.get(mBinding.timerSpinner.getSelectedItem()).charAt(0);
                if (selectedUsage != currentBinding.getUsage()) {
                    char lastUsage = currentBinding.getUsage();
                    changeItem(selectedUsage);
                    updateInfo(lastUsage);
                    currentBinding.setUsage(selectedUsage);
                    if (currentItem != null) {
                        currentBinding.deepCopy(currentItem);
                        currentBinding.updateValue();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void doRefresh() {
        LoadingDialog loadingDialog = new LoadingDialog(getContext());
        loadingDialog.showWithTimeout(3000);
        ToastUtil.showShortToast(getContext(), "正在刷新自动冲水定时器参数");
        doRefresh(0);
    }


    private void doRefresh(long delay) {
        if (initRequestHandler == null) {
            initRequestHandler = new Handler();
        }
        initRequestHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> params = new HashMap<>();
                params.put("toiletId", APPConstant.TOILETID);
                params.put("isBroadcast", "false");
                OkHttpManager.CommonPostAsyn(WebConstant.REQUEST_AUTO_WASH_TIMER, params, new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        Log.e(TAG, "获取自动冲水定时器参数阈值：" + WebConstant.REQUEST_AUTO_WASH_TIMER + " " + params.toString());
                    }
                });
            }
        }, delay);

    }

    private void doCommit() {

        String usage = String.valueOf(currentBinding.getUsage());
        if (StringUtils.isEmpty(currentBinding.getStartTime()) ||
                StringUtils.isEmpty(currentBinding.getEndTime()) ||
                StringUtils.isEmpty(currentBinding.getVoiceInterval()) ||
                StringUtils.isEmpty(currentBinding.getVoiceLength()) ||
                StringUtils.isEmpty(currentBinding.getBoosterPumpDelayTime())) {
            ToastUtil.showShortToast(getContext(), "参数阈值不能为空");
        } else {
            updateInfo(usage.charAt(0));
            OkHttpManager.CommonPostJson(WebConstant.SET_AUTO_WASH_TIMER, autoWashTimerInfo.toJson(), new OkHttpManager.ResultCallback() {
                @Override
                public void onCallBack(OkHttpManager.State state, String result) {
                    Log.e(TAG, "设置自动冲水定时器参数阈值：" + WebConstant.SET_AUTO_WASH_TIMER + " " + autoWashTimerInfo.toJson());
                }
            });
        }
    }


//    @Override
//    public void setAutoWashTimerSocket(AutoWashTimerBinding binding) {
//        //将binding对象存到map中，分为男厕，女厕，残卫
//        timerInfoMap.put(binding.getUsage(),binding);
//        System.out.println("binding.getUsage="+binding.getUsage()+"  currentBinding.getUsage()===="+currentBinding.getUsage());
//        if (binding.getUsage()  == currentBinding.getUsage()){
//            //不能直接将binding 赋值给currentbinding  那样currentbinding的地址就变了，跟XML文件中绑定的对象不一样，只能通过深度复制，不改变对象本身地址
//            this.currentBinding.deepCopy(binding);
//            currentBinding.updateValue();
//        }
//    }

    @Override
    public void setAutoWashTimerSocket(AutoWashTimerInfo info) {
        ToastUtil.logAndToast(getContext(), "定时器参数数据");
        this.autoWashTimerInfo = info;
        changeItem(currentBinding.getUsage());
        currentBinding.deepCopy(currentItem);
        currentBinding.updateValue();
    }
}
