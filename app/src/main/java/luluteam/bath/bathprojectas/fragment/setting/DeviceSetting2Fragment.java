package luluteam.bath.bathprojectas.fragment.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.databinding.FragmentDeviceSetting2Binding;
import luluteam.bath.bathprojectas.fragment.main.ManageSetFragment;
import luluteam.bath.bathprojectas.model.ParamsInfo;
import luluteam.bath.bathprojectas.model.binding.ParamsInfoBinding;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.DatePickDialogUtil;
import luluteam.bath.bathprojectas.utils.DensityUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;
import luluteam.bath.bathprojectas.view.dialog.datePickDialog.DatePick;

/**
 * Created by 123 on 2017/11/16.
 */

public class DeviceSetting2Fragment extends Fragment implements ManageSetFragment.ParamsCallback {

    /**
     * 一键系列
     */
//一键设置全部
//    private CheckBox setAll_cb;
    //一键恢复默认设置
    private Button defaultAll_btn;
    private Button refreshAll_btn;

    /**
     * 采集卡系列
     */
//采用系统时间
    private CheckBox systemTime_cb;
    //采集卡用户输入时间
    private EditText cardTime_et;
    //采集卡确定提交时间
    private Button cardTime_btn;

    /**
     * 光照探头系列
     */
//探头厕所类型选择
    private Spinner lightDetector_spinner;
    //探头阈值设定
    private EditText lightDetector_et;
    //探头确定设置
    private Button lightDetector_btn;

    /**
     * 定时器系列
     */
//定时器厕所类型选择
    private Spinner timer_spinner;
    //辅灯开始时间
    private EditText fudeng_startTime_et;
    //辅灯结束时间
    private EditText fudeng_endTime_et;
    //主灯延时时间
    private EditText zhudeng_delayTime_et;
    //音响开始时间
    private EditText audioTimer_startTime_et;
    //音响结束时间
    private EditText audioTimer_endTime_et;
    //音响延时时间
    private EditText audioTimer_delayTime_et;
    //消毒开始时间
    private EditText xiaodu_startTime_et;
    //消毒结束时间
    private EditText xiaodu_endTime_et;
    //消毒延时时间
    private EditText xiaodu_delayTime_et;
    //灯确定
    private Button lightTimer_btn;
    //音响确定
    private Button audioTimer_btn;
    //消毒确定
    private Button xiaodu_btn;

    private EditText adjusted_value_et;
    private Button adjusted_value_btn;
    private Button refresh_value_btn;

    private Context mContext;

    //用来接收socket的数据参数
    private ParamsInfo paramsInfo;
    private ParamsInfoBinding paramsInfoBinding;
    private static final String[] toiletItem = {"男厕", "女厕", "残卫"};
    private static final String TAG = "DeviceSetting2Fragment";
    private FragmentDeviceSetting2Binding mBinding;

    //用于标记“刷新操作”“设置操作”
    private enum RequestType {
        RequestSet, RequestRefresh
    }

    //用于记录当前请求的类型
    private RequestType currentType = RequestType.RequestRefresh;

    private String lastToiletId = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_setting2, container, false);
        View view = mBinding.getRoot();
        mContext = getContext();
        this.paramsInfoBinding = new ParamsInfoBinding();
        mBinding.setCurrentBinding(paramsInfoBinding);
        initView(view);
        setChooseTime();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //这里的一个loading是配合onStart()生命周期中的刷新参数以及读取水表校正值
        LoadingDialog loadingDialog = new LoadingDialog(mContext);
        loadingDialog.showWithTimeout(3000);
        Log.e(TAG, "设备设置fragment=====onActivityCreated");
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!lastToiletId.equals(APPConstant.TOILETID)) {
            //每次进入该Activity，paramsInfo均置为null，
            // 然后再从服务器上重新获取参数阈值数据，保证当前参数阈值为最新的
            this.paramsInfo = null;
            refreshParams();
            refreshWaterAdjustedValue();
            ToastUtil.showShortToast(mContext, "正在刷新参数阈值");
            Log.e(TAG, "设备设置fragment=====onStart");
            lastToiletId = APPConstant.TOILETID;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("+++++++++++++onPause()");
        clearETFocus(fudeng_startTime_et);
        clearETFocus(fudeng_endTime_et);
        clearETFocus(xiaodu_startTime_et);
        clearETFocus(xiaodu_endTime_et);
        clearETFocus(audioTimer_startTime_et);
        clearETFocus(audioTimer_endTime_et);
        clearETFocus(cardTime_et);
    }

    private void clearETFocus(EditText editText) {
        if (editText.isFocused()) {
            editText.clearFocus();
        }
    }

    private void initView(View view) {
//        setAll_cb = (CheckBox) view.findViewById(R.id.setAll_cb);
        defaultAll_btn = (Button) view.findViewById(R.id.defaultAll_btn);
        refreshAll_btn = (Button) view.findViewById(R.id.refreshAll_btn);

        systemTime_cb = (CheckBox) view.findViewById(R.id.systemTime_cb);
        cardTime_et = (EditText) view.findViewById(R.id.cardTime_et);
        cardTime_btn = (Button) view.findViewById(R.id.cardTime_btn);

        lightDetector_spinner = (Spinner) view.findViewById(R.id.lightDetector_spinner);
        lightDetector_et = (EditText) view.findViewById(R.id.lightDetector_et);
        lightDetector_btn = (Button) view.findViewById(R.id.lightDetector_btn);

        timer_spinner = (Spinner) view.findViewById(R.id.timer_spinner);
        fudeng_startTime_et = (EditText) view.findViewById(R.id.fudeng_startTime_et);
        fudeng_endTime_et = (EditText) view.findViewById(R.id.fudeng_endTime_et);
        zhudeng_delayTime_et = (EditText) view.findViewById(R.id.zhudeng_delayTime_et);
        lightTimer_btn = (Button) view.findViewById(R.id.lightTimer_btn);

        audioTimer_startTime_et = (EditText) view.findViewById(R.id.audioTimer_startTime_et);
        audioTimer_endTime_et = (EditText) view.findViewById(R.id.audioTimer_endTime_et);
        audioTimer_delayTime_et = (EditText) view.findViewById(R.id.audioTimer_delayTime_et);
        audioTimer_btn = (Button) view.findViewById(R.id.audioTimer_btn);

        xiaodu_startTime_et = (EditText) view.findViewById(R.id.xiaodu_startTime_et);
        xiaodu_endTime_et = (EditText) view.findViewById(R.id.xiaodu_endTime_et);
        xiaodu_delayTime_et = (EditText) view.findViewById(R.id.xiaodu_delayTime_et);
        xiaodu_btn = (Button) view.findViewById(R.id.xiaodu_btn);

        adjusted_value_et = (EditText) view.findViewById(R.id.adjusted_value_et);
        adjusted_value_btn = (Button) view.findViewById(R.id.adjusted_value_btn);
        refresh_value_btn = (Button) view.findViewById(R.id.refresh_value_btn);


        /**
         * 初始化Usage的Spinner的value
         *
         */
        List<String> list = Arrays.asList(toiletItem);
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lightDetector_spinner.setDropDownVerticalOffset(DensityUtil.dip2px(mContext, 20));
        lightDetector_spinner.setAdapter(adapter);
        timer_spinner.setAdapter(adapter);
        timer_spinner.setDropDownVerticalOffset(DensityUtil.dip2px(mContext, 20));

        setSomeListener();

    }

    /**
     * 添加Listener
     */
    private void setSomeListener() {

        //恢复默认设置
        defaultAll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog loadingDialog = new LoadingDialog(mContext);
                loadingDialog.showWithTimeout(3000);
                currentType = RequestType.RequestRefresh;
                setDefaultAll();
            }
        });

        //刷新，获取参数阈值
        refreshAll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog loadingDialog = new LoadingDialog(mContext);
                loadingDialog.showWithTimeout(3000);
                currentType = RequestType.RequestRefresh;
                refreshParams();
            }
        });

        //设置系统时钟——采用系统时间
        systemTime_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //禁用用户设置采集卡时间
                    cardTime_et.setEnabled(false);
                } else {
                    //启用用户设置采集卡时间
                    cardTime_et.setEnabled(true);
                }
            }
        });

        //设置系统时钟
        cardTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCurrentParams()) {
                    LoadingDialog loadingDialog = new LoadingDialog(mContext);
                    loadingDialog.showWithTimeout(3000);
                    currentType = RequestType.RequestSet;
                    setSystemTime();
                }
            }
        });

        //设置探头阈值
        lightDetector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCurrentParams()) {
                    LoadingDialog loadingDialog = new LoadingDialog(mContext);
                    loadingDialog.showWithTimeout(3000);
                    currentType = RequestType.RequestSet;
                    setLightDetector();
                }
            }
        });

        //设置定时器参数
        View.OnClickListener timerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkCurrentParams()) {
                    return;
                }
                LoadingDialog loadingDialog = new LoadingDialog(mContext);
                loadingDialog.showWithTimeout(3000);
                currentType = RequestType.RequestSet;
                switch (v.getId()) {
                    case R.id.lightTimer_btn:
                        setTimer("L");
                        break;
                    case R.id.audioTimer_btn:
                        setTimer("M");
                        break;
                    case R.id.xiaodu_btn:
                        setTimer("N");
                        break;
                }
            }
        };
        audioTimer_btn.setOnClickListener(timerListener);
        xiaodu_btn.setOnClickListener(timerListener);
        lightTimer_btn.setOnClickListener(timerListener);


        /**
         * 切换男女厕的时候，动态更新数据
         */
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (paramsInfo != null) {
                    paramsInfoBinding.updateValue(paramsInfo.getValue()
                            , lightDetector_spinner.getSelectedItemPosition(), timer_spinner.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        lightDetector_spinner.setOnItemSelectedListener(spinnerListener);
        timer_spinner.setOnItemSelectedListener(spinnerListener);


        //设置水表校正值
        adjusted_value_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWaterAdjustedValue();
            }
        });
        refresh_value_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWaterAdjustedValue();
            }
        });

        mBinding.weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showShortToast(getContext(), "发送天气数据...");
                Repository.sendWeatherInfo(APPConstant.TOILETID, 0);
                ToastUtil.showShortToast(getContext(), "发送厕所名称信息数据...");
                Repository.sendToiletBasicInfo(APPConstant.TOILETID, 5);
            }
        });
    }


    /**
     * 1、设置采集卡校准系统时间
     */
    private void setSystemTime() {
        HashMap<String, String> map = new HashMap<>();
        String time = "";
        map.put("toiletId", APPConstant.TOILETID);
        map.put("isBroadcast", "false");

        //true采用系统时间，false则采用用户输入的时间
        if (!systemTime_cb.isChecked()) {
            time = cardTime_et.getText().toString();
            map.put("time", time);
        }

        OkHttpManager.CommonPostAsyn(WebConstant.SET_SYSTEM_CLOCK, map, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                System.out.println(result);
                if (state == OkHttpManager.State.SUCCESS) {
                    refreshParamsDelay();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(mContext, "设置失败：" + result);
                        }
                    });

                }


            }
        });
    }


    /**
     * 2、设置探头:光照探头
     */
    private void setLightDetector() {
        String value = paramsInfoBinding.getThresholdLight().getThresholdValue();
        String usage = paramsInfoBinding.getThresholdLight().getUsage();
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(usage)) {
            ToastUtil.showShortToast(mContext, "请输入数值！！");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("toiletId", APPConstant.TOILETID);
        map.put("usage", usage);
        map.put("deviceType", "J");
        map.put("value", value);
        map.put("isBroadcast", "false");
        OkHttpManager.CommonPostAsyn(WebConstant.SET_THRESHOLD, map, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                System.out.println(result);
                if (state == OkHttpManager.State.SUCCESS) {
                    refreshParamsDelay();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(mContext, "设置失败：" + result);
                        }
                    });
                }

            }
        });


    }

    /**
     * 根据输入的设备类型，设置相应的开始，结束，延时，时间
     *
     * @param deviceType L,M,N
     */
    private void setTimer(String deviceType) {
        String startTime = "";
        String endTime = "";
        String delayTime = "";
        switch (deviceType) {
            case "L":
                startTime = paramsInfoBinding.getGroup().getTimerLight().getStartTime();
                endTime = paramsInfoBinding.getGroup().getTimerLight().getStopTime();
                delayTime = paramsInfoBinding.getGroup().getTimerLight().getDuration();
                break;
            case "M":
                startTime = paramsInfoBinding.getGroup().getTimerAudio().getStartTime();
                endTime = paramsInfoBinding.getGroup().getTimerAudio().getStopTime();
                delayTime = paramsInfoBinding.getGroup().getTimerAudio().getDuration();
                break;
            case "N":
                startTime = paramsInfoBinding.getGroup().getTimerSterilamp().getStartTime();
                endTime = paramsInfoBinding.getGroup().getTimerSterilamp().getStopTime();
                delayTime = paramsInfoBinding.getGroup().getTimerSterilamp().getDuration();
                break;
            default:
                break;
        }
//        String usage = APPConstant.UsageMap.get(timer_spinner.getSelectedItem().toString());
        String usage = paramsInfoBinding.getGroup().getUsage();

        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime) || StringUtils.isEmpty(delayTime)) {
            ToastUtil.showShortToast(mContext, "请输入数值！");
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("toiletId", APPConstant.TOILETID);
            map.put("usage", usage);
            map.put("deviceType", deviceType);
            map.put("time", Integer.parseInt(delayTime) * 60 + "");
            map.put("startTime", startTime);
            map.put("stopTime", endTime);
            map.put("isBroadcast", "false");
            OkHttpManager.CommonPostAsyn(WebConstant.SET_TIMER, map, new OkHttpManager.ResultCallback() {
                @Override
                public void onCallBack(OkHttpManager.State state, String result) {
                    System.out.println(result);

                    if (state == OkHttpManager.State.SUCCESS) {
                        refreshParamsDelay();
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast(mContext, "设置失败：" + result);

                            }
                        });
                    }

                }
            });
        }
    }

    /**
     * 4、一键恢复默认设置
     */
    private void setDefaultAll() {
        HashMap<String, String> map = new HashMap<>();
        map.put("toiletId", APPConstant.TOILETID);
        map.put("isBroadcast", "false");
        OkHttpManager.CommonPostAsyn(WebConstant.SET_DEFAULT_ALL, map, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                System.out.println(result);

                if (state == OkHttpManager.State.SUCCESS) {
                    refreshParamsDelay();
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(mContext, "设置失败：" + result);

                        }
                    });
                }
            }
        });
    }

    /**
     * 刷新，获取参数阈值
     */
    private void refreshParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        params.put("isBroadcast", "false");
        OkHttpManager.CommonPostAsyn(WebConstant.GET_PARAMS, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.e(TAG, "获取参数阈值：" + WebConstant.GET_PARAMS + " " + params.toString());
            }
        });
    }

    /**
     * 危险：每次设置参数后，自动刷新参数阈值
     * 临时解决方案
     * 非主线程调用
     */
    @Deprecated
    private void refreshParamsDelay() {
        try {
            Thread.sleep(1500);
            refreshParams();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新，获取水表校正值
     */
    private void refreshWaterAdjustedValue() {

        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_WATER_ADJUSTED_VALUE, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.d(TAG, "读取水表校正值:" + result);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == OkHttpManager.State.SUCCESS) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (Boolean.parseBoolean(jsonObject.get("result").toString())) {
//                                    ToastUtil.showShortToast(mContext, "获取水表校正值成功~！");
                                    double value = jsonObject.getDouble("value");
                                    updateWaterAdjustedValueUI(value);
                                } else {
                                    ToastUtil.showShortToast(mContext, "获取水表校正值失败~！ " + result);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            ToastUtil.showShortToast(mContext, "操作失败：" + result);
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置水表校正值
     */
    private void setWaterAdjustedValue() {
        String value = adjusted_value_et.getText().toString();
        if (StringUtils.isEmpty(value)) {
            ToastUtil.showShortToast(mContext, "数值不能为空");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        params.put("value", value);
        params.put("isBroadcast", "false");
        OkHttpManager.CommonPostAsyn(WebConstant.SET_WATER_ADJUSTED_VALUE, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {

                Log.d(TAG, "设置水表校正值:" + result);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == OkHttpManager.State.SUCCESS) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (Boolean.parseBoolean(jsonObject.get("result").toString())) {
                                    ToastUtil.showShortToast(mContext, "设置成功~！");
                                } else {
                                    ToastUtil.showShortToast(mContext, "设置失败: " + result);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            ToastUtil.showShortToast(mContext, "设置失败：" + result);
                        }
                    }
                });

            }


        });
    }


    //=================================================================================
    //
    //=================================================================================

    /**
     * 对一些需要 选择时间 的EditText,使用该方法
     */
    private void setChooseTime() {
        /**
         * 弹出DatePicker
         */
        DatePickDialogUtil.setEditTextFocus(getContext(), cardTime_et, DatePick.TYPE_ALL);
        DatePickDialogUtil.setEditTextFocus(getContext(), fudeng_startTime_et, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), fudeng_endTime_et, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), xiaodu_startTime_et, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), xiaodu_endTime_et, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), audioTimer_startTime_et, DatePick.TYPE_ONLYTIME);
        DatePickDialogUtil.setEditTextFocus(getContext(), audioTimer_endTime_et, DatePick.TYPE_ONLYTIME);
    }


    //==============================================================================
    //更新参数阈值
    //==============================================================================

    /**
     * 更新参数阈值
     *
     * @param paramsInfo
     */
    @Override
    public void setParamsBySocket(ParamsInfo paramsInfo) {
        System.out.println("DeviceSetting2Fragment  更新全体参数阈值：" + Thread.currentThread().toString() + " " + paramsInfo.toJson());
        if (getParentFragment() == null) {
            Log.e(TAG, "该Fragment还未加载");
        }
        if (currentType == RequestType.RequestSet) {
            //说明此次操作是通过 设置某一个参数操作 后，返回的数据
            //没有作比较，直接显示“成功”
            try {
                new AlertDialog.Builder(mContext).setTitle("提示")
                        .setMessage("设置成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showShortToast(mContext, "设置成功~！");
            }

        } else {
            ToastUtil.showShortToast(mContext, "参数阈值数据刷新成功~！");
        }

        this.paramsInfo = paramsInfo;
        //更新值的显示
        paramsInfoBinding.updateValue(this.paramsInfo.getValue()
                , lightDetector_spinner.getSelectedItemPosition()
                , timer_spinner.getSelectedItemPosition());

        currentType = RequestType.RequestRefresh;

    }


    /**
     * 更新水表校正值的显示
     *
     * @param value
     */
    private void updateWaterAdjustedValueUI(double value) {
//        adjusted_value_et.setText(value + "");
        adjusted_value_et.setText(String.format("%.2f", value));

    }

    /**
     * 判断 记录当前设备参数的currentParams 是否有效
     * true 有效：!=null，即已经通过接口获得了参数数据；并且id相同
     *
     * @return
     */
    private boolean checkCurrentParams() {
        if (this.paramsInfo != null && this.paramsInfo.getToiletId().equals(APPConstant.TOILETID)) {
            return true;
        } else {
            ToastUtil.showShortToast(mContext, "为保证数据一致性，请先成功刷新后再操作");
            return false;
        }
    }


}
