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

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.databinding.FragmentAutoWashTimer2Binding;
import luluteam.bath.bathprojectas.fragment.main.ManageSetFragment;
import luluteam.bath.bathprojectas.model.AutoWashTimer2Info;
import luluteam.bath.bathprojectas.model.binding.AutoWashTimer2Binding;
import luluteam.bath.bathprojectas.utils.DatePickDialogUtil;
import luluteam.bath.bathprojectas.utils.TimeUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;
import luluteam.bath.bathprojectas.view.dialog.datePickDialog.DatePick;

public class AutoWashTimer2Fragment extends Fragment implements ManageSetFragment.AutoWashTimer2Callback {
    private static final String TAG = "AutoWashTimer2Fragment";

    private FragmentAutoWashTimer2Binding mBinding;
    private AutoWashTimer2Binding currentBinding;
//    private AutoWashTimer2Info defaultInfo;

    private static Handler initRequestHandler = null;

    private String lastToiletId = "";

    private AutoWashTimer2Info autoWashTimer2Info = new AutoWashTimer2Info();

    public AutoWashTimer2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_auto_wash_timer2, container, false);
        View view = mBinding.getRoot();
        this.currentBinding = new AutoWashTimer2Binding(APPConstant.UsageMap.get("男厕").charAt(0),
                'X', "00:00:00", "00:40:00", "20", "15", "0");
        mBinding.setCurrentBinding(currentBinding);
        setChooseTimer();
        initListeners();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!lastToiletId.equals(APPConstant.TOILETID)) {
            //自动请求两次，没办法的事。因为下位机处理性能有限，有时候会不响应
            doRefresh(5000);
            doRefresh(3000);
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
//                doCommit();
                doRefresh(3000);
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

//        String usage = String.valueOf(currentBinding.getUsage());
        if (StringUtils.isEmpty(currentBinding.getStartTime()) ||
                StringUtils.isEmpty(currentBinding.getEndTime()) ||
                StringUtils.isEmpty(currentBinding.getWashDelay()) ||
                StringUtils.isEmpty(currentBinding.getVoiceLength()) ||
                StringUtils.isEmpty(currentBinding.getBoosterPumpDelayTime())) {
            ToastUtil.showShortToast(getContext(), "参数阈值不能为空");
        } else {
            if (updateInfo()) {
//            updateInfo(usage.charAt(0));
                OkHttpManager.CommonPostJson(WebConstant.SET_AUTO_WASH_TIMER, autoWashTimer2Info.toJson(), new OkHttpManager.ResultCallback() {
                    @Override
                    public void onCallBack(OkHttpManager.State state, String result) {
                        Log.e(TAG, "设置自动冲水定时器参数阈值：" + WebConstant.SET_AUTO_WASH_TIMER + " " + autoWashTimer2Info.toJson());
                    }
                });
            } else {
                ToastUtil.showShortToast(getContext(), "数据校验失败，请检查输入...");
            }
        }
    }

    /**
     * 对输入的数据进行有效性检验，然后在发送给服务器
     *
     * @return
     */
    private boolean updateInfo() {
        try {
            int washDelay = Integer.parseInt(currentBinding.getWashDelay());
            if (TimeUtil.getDiffTime(currentBinding.getStartTime(), currentBinding.getEndTime(), "HH:mm:ss") < washDelay * 60) {
                ToastUtil.showShortToast(getContext(), "开始时间与结束时间之间的间隔需要大于延时时间");
                return false;
            }
            autoWashTimer2Info.setToiletId(APPConstant.TOILETID);
            //usage这个字段无意义
            autoWashTimer2Info.setUsage(APPConstant.UsageMap.get("男厕").charAt(0));
            autoWashTimer2Info.setDeviceType('X');
            autoWashTimer2Info.startTime = TimeUtil.getTime(currentBinding.getStartTime(), "HH:mm:ss", "HHmmss");
            autoWashTimer2Info.endTime = TimeUtil.getTime(currentBinding.getEndTime(), "HH:mm:ss", "HHmmss");
            autoWashTimer2Info.washDelay = washDelay;
            autoWashTimer2Info.voiceLength = Integer.parseInt(currentBinding.getVoiceLength());
            autoWashTimer2Info.boosterPumpDelayTime = Integer.parseInt(currentBinding.getBoosterPumpDelayTime());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void info2binding(AutoWashTimer2Info info) {
        currentBinding.setStartTime(info.startTime);
        currentBinding.setEndTime(info.endTime);
        currentBinding.setWashDelay(info.washDelay + "");
        currentBinding.setVoiceLength(info.voiceLength + "");
        currentBinding.setBoosterPumpDelayTime(info.boosterPumpDelayTime + "");
        currentBinding.updateValue();
    }

    @Override
    public void setAutoWashTimerSocket(AutoWashTimer2Info info) {
        Log.e(TAG, info.toString());
        ToastUtil.logAndToast(getContext(), "冲水定时器参数刷新成功");
        this.autoWashTimer2Info = info;
        info2binding(info);
    }
}
