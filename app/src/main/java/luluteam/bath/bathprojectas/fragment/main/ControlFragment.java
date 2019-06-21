package luluteam.bath.bathprojectas.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.activity.DisplayImgsAty;
import luluteam.bath.bathprojectas.adapter.ControlAdapter;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.control.RemoteControlFragment;
import luluteam.bath.bathprojectas.fragment.statistics.AmountStatsFragment;
import luluteam.bath.bathprojectas.fragment.statistics.HumanFlowStatsFragment;
import luluteam.bath.bathprojectas.model.RemoteControl.AllDevicesMessage;
import luluteam.bath.bathprojectas.model.RemoteControl.DeviceMessage;
import luluteam.bath.bathprojectas.model.RemoteControl.Devices;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.result.FindFileResult;
import luluteam.bath.bathprojectas.model.result.LockStateResult;
import luluteam.bath.bathprojectas.model.result.ValueResult;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.tools.JumpHelper;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.TabItemViewCtrlFragment;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

import static luluteam.bath.bathprojectas.constants.APPConstant.deviceNames;


/**
 * 显示 男女残卫 单个设备的控制状态
 * 2019/05/28——增加了滑动手势
 */
public class ControlFragment extends BaseFragment implements View.OnTouchListener, BaseFragment.ControlCallback {

    private Devices manDevices = new Devices("男厕");
    private Devices womanDevices = new Devices("女厕");
    private Devices disabledDevices = new Devices("残卫");

    private RemoteControlFragment manFragment = new RemoteControlFragment();
    private RemoteControlFragment womanFragment = new RemoteControlFragment();
    private RemoteControlFragment disabledFragment = new RemoteControlFragment();

    private ControlAdapter manAdapter;
    private ControlAdapter womanAdapter;
    private ControlAdapter disabledAdapter;

    private TextView waterMeterTv;//水表数据显示控件
    private TextView elecMeterTv;//电表数据显示控件
    private TextView bodyDetectorTv;//人流量数据显示控件
    private LinearLayout waterMeter_ll;
    private LinearLayout elecMeter_ll;
    private LinearLayout peculiarSmell_ll;
    private LinearLayout cleanliness_ll;
    private LinearLayout bodyFlow_ll;

    private ImageView iv_arrow;
    private ImageView toiletInfo_img;
    private ImageView toiletState_img;
    private List<String> toiletInfo_imgUrls;

    private TextView gasLevelTv;
    private TextView clealinessLevelTv;

    private TextView tv_video;
    private TextView btn_video;

    private boolean allControlled = false;//远程控制或者禁止远程控制的总开关状态
    private ToggleButton allCotrolled_btn;//远程控制总开关
    private boolean lockedToilet = false;//锁定厕所和释放厕所总开关的状态
    private ToggleButton lockToilet_btn;//锁定厕所状态开关
    private Button refreshState_btn;//刷新按钮

    private ViewPager remoteCtrl_viewpager;
    private TabLayout remoteCtrl_tablayout;
    private List<String> titleList = new ArrayList<>();
    private List<BaseFragment> fragmentList = new ArrayList<>();
    private List<TabItemViewCtrlFragment> tabItemViewList = new ArrayList<>();

    public static final int DIALOG_TIME_DELAY = 2500;
    private static final String TAG = "ControlFragment";

    private Context context;
    private LoadingDialog loadingDialog;

    public static long wholeStateTimestamp = 0;
    public static final int INTERVAL = 45000;

    /**
     * 用来包裹所有非列表的布局，实现滑动显示和隐藏数据分析模块的布局
     */
    private RelativeLayout canSlideRl;
    /**
     * 包裹数据分析模块
     */
    private LinearLayout canGoneLl;


    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        context = getActivity();
        loadingDialog = new LoadingDialog(context);
        initUI(view);
        initData();
        //刷新
        doRefresh();
        getYesterdayBodyDetectorMaxValue();
        checkToiletLock();

        setEventBus(this, true);
        return view;
    }

    /**
     * 初始化UI界面
     */
    private void initUI(View view) {

        allCotrolled_btn = (ToggleButton) view.findViewById(R.id.allControlled_btn);
        lockToilet_btn = (ToggleButton) view.findViewById(R.id.lockToilet_btn);
        refreshState_btn = (Button) view.findViewById(R.id.refreshState_btn);//获取当前设备状态的按钮

        //图片信息
        toiletInfo_img = (ImageView) view.findViewById(R.id.toiletInfo_img);
        toiletState_img = (ImageView) view.findViewById(R.id.toiletState_img);

        remoteCtrl_tablayout = (TabLayout) view.findViewById(R.id.remoteCtrl_tab);
        remoteCtrl_viewpager = (ViewPager) view.findViewById(R.id.container);

        waterMeterTv = (TextView) view.findViewById(R.id.waterMeterTv);
        elecMeterTv = (TextView) view.findViewById(R.id.elecMeterTv);
        waterMeter_ll = (LinearLayout) view.findViewById(R.id.waterMeter_ll);
        elecMeter_ll = (LinearLayout) view.findViewById(R.id.elecMeter_ll);

        bodyFlow_ll = (LinearLayout) view.findViewById(R.id.body_flow_ll);
        bodyDetectorTv = (TextView) view.findViewById(R.id.bodyDetectorTv);

        //清洁度异味
        peculiarSmell_ll = (LinearLayout) view.findViewById(R.id.peculiarSmell_ll);
        gasLevelTv = (TextView) view.findViewById(R.id.gas_level_tv);
        cleanliness_ll = (LinearLayout) view.findViewById(R.id.cleanliness_ll);
        clealinessLevelTv = (TextView) view.findViewById(R.id.cleanliness_level_tv);


        tv_video = (TextView) view.findViewById(R.id.tv_video);
        btn_video = (Button) view.findViewById(R.id.btn_video);

        canGoneLl = (LinearLayout) view.findViewById(R.id.can_gone_ll);
        canSlideRl = (RelativeLayout) view.findViewById(R.id.can_slide_ll);

        iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow_up);
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_arrow);
        iv_arrow.startAnimation(animationSet);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToiletImgInfo(canGoneLl.getVisibility() == View.GONE);
            }
        });
        allCotrolled_btn.setOnTouchListener(this);
        lockToilet_btn.setOnTouchListener(this);
        refreshState_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRefresh();
                checkToiletLock();
            }
        });
        //=============================
        setGestureListener(toiletInfo_img);
        setGestureListener(toiletState_img);
        setGestureListener(canSlideRl);
        //===============================
        waterMeter_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(APPConstant.TOILETID)) {
                    ToastUtil.logAndToast(context, "请选择厕所");
                    return;
                }
                JumpHelper.gotoDisplayFragmentActivity(context, AmountStatsFragment.class, "统计分析");
            }
        });
        elecMeter_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(APPConstant.TOILETID)) {
                    ToastUtil.logAndToast(context, "请选择厕所");
                    return;
                }
                JumpHelper.gotoDisplayFragmentActivity(context, AmountStatsFragment.class, "统计分析");

            }
        });
        cleanliness_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (org.apache.commons.lang.StringUtils.isNotEmpty(APPConstant.TOILETID)) {
                    JumpHelper.gotoPitActivity(context, APPConstant.TOILETID, 1);
                } else {
                    ToastUtil.showShortToast(context, "请先选择厕所");
                }

            }
        });
        peculiarSmell_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (org.apache.commons.lang.StringUtils.isNotEmpty(APPConstant.TOILETID)) {
                    JumpHelper.gotoPitActivity(context, APPConstant.TOILETID, 2);
                } else {
                    ToastUtil.showShortToast(context, "请先选择厕所");
                }
            }
        });

        bodyFlow_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(APPConstant.TOILETID)) {
                    ToastUtil.logAndToast(context, "请选择厕所");
                    return;
                }
                JumpHelper.gotoDisplayFragmentActivity(context, HumanFlowStatsFragment.class, "统计分析");
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpHelper.gotoVideoActivity(context, APPConstant.TOILETID);
            }
        });

        RemoteControlFragment.OnIdelListener onIdelListener = new RemoteControlFragment.OnIdelListener() {
            @Override
            public void onTop() {
                showToiletImgInfo(true);
            }

            @Override
            public void onBottom() {
                showToiletImgInfo(false);
            }
        };
        manFragment.setListener(onIdelListener);
        womanFragment.setListener(onIdelListener);
        disabledFragment.setListener(onIdelListener);
    }


    /**
     * 设置上下滑动作监听器
     *
     * @author jczmdeveloper
     */
    float mPosX, mPosY, mCurPosX, mCurPosY;

    private void setGestureListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0
                                && (Math.abs(mCurPosY - mPosY) > 120)) {
                            //向下滑動
                            Log.e(TAG, "向下滑动");
                            showToiletImgInfo(true);
                        } else if (mCurPosY - mPosY < 0
                                && (Math.abs(mCurPosY - mPosY) > 120)) {
                            //向上滑动
                            Log.e(TAG, "向上滑动");
                            showToiletImgInfo(false);
                        } else {
                            Log.e(TAG, "点击");
                            if (view.getId() == toiletInfo_img.getId()) {
                                Intent intent = new Intent(context, DisplayImgsAty.class);
                                intent.putExtra("businessType", "toiletInfoImg");
                                startActivity(intent);
                            } else if (view.getId() == toiletState_img.getId()) {
                                //TODO: 0702厕所状态详情界面
                                if (org.apache.commons.lang.StringUtils.isNotEmpty(APPConstant.TOILETID)) {
                                    JumpHelper.gotoPitActivity(context, APPConstant.TOILETID, 1);
                                } else {
                                    ToastUtil.showShortToast(context, "请先选择厕所");
                                }
                            }
                        }
                        mPosX = mPosY = mCurPosY = mCurPosX = 0;

                        break;
                    default:

                }
                return true;
            }

        });
    }

    private void showToiletImgInfo(boolean isShow) {
        AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.anim_arrow);
        if (isShow) {
            iv_arrow.setImageResource(R.drawable.iv_up_press);
            iv_arrow.startAnimation(animationSet);
            canGoneLl.setVisibility(View.VISIBLE);
            canGoneLl.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_top));
        } else {
            iv_arrow.setImageResource(R.drawable.iv_down_press);
            iv_arrow.startAnimation(animationSet);
            // 必须先clear，也不知道什么原因
            canGoneLl.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_top);
            canGoneLl.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    canGoneLl.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    @Override
    public void onReloadImage() {
        if (toiletInfo_img != null) {
            Log.e(TAG, "reload Image......");
            toiletInfo_img.setImageDrawable(getResources().getDrawable(R.drawable.add_img));
            loadImgsUrl();
        }
    }

    @Override
    public void onNewToilet() {
        //将时间戳置为0,防止切换厕所时，整体状态没有过来
        wholeStateTimestamp = 0;
        //将电表读数，水表读数，人流量都重置为0，防止整体状态没有过来

        setTextView(elecMeterTv, "0.00");
        setTextView(waterMeterTv, "0.00");
        setTextView(bodyDetectorTv, "0");
        doRefresh();
        getYesterdayBodyDetectorMaxValue();
    }

    private void setTextView(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    /**
     * 获取一组图片的url
     */
    private void loadImgsUrl() {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        params.put("businessType", "toiletInfoImg");
        OkHttpManager.CommonPostAsyn(WebConstant.GET_IMGS_URLS_BY_TOILETID, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
                    toiletInfo_imgUrls.clear();
                    FindFileResult findFileResult = new Gson().fromJson(result, FindFileResult.class);
                    if (findFileResult.isResult()) {
                        for (FindFileResult.FileInfo fileInfo : findFileResult.getDataList()) {
                            String url = WebConstant.DOWNLOAD_IMG_BY_FILEID + "?fileId=" + fileInfo.getFileId();
                            toiletInfo_imgUrls.add(url);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showImgs();
                            }
                        });
                    }
                }
            }
        });

    }

    @MainThread
    private void showImgs() {
        Log.e(TAG, "showImage...");
        if (toiletInfo_imgUrls != null && toiletInfo_imgUrls.size() > 0) {
            Glide.with(context)
                    .load(toiletInfo_imgUrls.get(0))
                    .into(toiletInfo_img);
        } else {
            toiletInfo_img.setImageDrawable(getResources().getDrawable(R.drawable.add_img));
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        manAdapter = new ControlAdapter(manDevices, context);
        womanAdapter = new ControlAdapter(womanDevices, context);
        disabledAdapter = new ControlAdapter(disabledDevices, context);

        manFragment.setUsage("男厕");
        manFragment.setAdapter(manAdapter);
        womanFragment.setUsage("女厕");
        womanFragment.setAdapter(womanAdapter);
        disabledFragment.setUsage("残卫");
        disabledFragment.setAdapter(disabledAdapter);

        fragmentList.add(manFragment);
        fragmentList.add(womanFragment);
        fragmentList.add(disabledFragment);
        tabItemViewList.add(new TabItemViewCtrlFragment(getContext(), R.drawable.tab_item_man_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(getContext(), R.drawable.tab_item_woman_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(getContext(), R.drawable.tab_item_third_selector, 0));

        //fragment嵌套fragment，调用getChildFragmentManager
        remoteCtrl_viewpager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        //同时可加载3个fragment，不销毁view
        remoteCtrl_viewpager.setOffscreenPageLimit(3);
        remoteCtrl_tablayout.setupWithViewPager(remoteCtrl_viewpager);
        remoteCtrl_tablayout.getTabAt(1).select();
        /**
         * MODE_FIXED:固定tabs，并同时显示所有的tabs。
         * MODE_SCROLLABLE：可滚动tabs，显示一部分tabs，在这个模式下能包含长标签和大量的tabs，最好用于用户不需要直接比较tabs。
         */
        remoteCtrl_tablayout.setTabMode(TabLayout.MODE_FIXED);
        //手动指定TabLayout中Item的View
        for (int i = 0; i < remoteCtrl_tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = remoteCtrl_tablayout.getTabAt(i);
            tab.setCustomView(tabItemViewList.get(i));
        }

//        toiletState_imgUrls = new ArrayList<>();
        toiletInfo_imgUrls = new ArrayList<>();
        loadImgsUrl();
    }

    /**
     * （取消）注册 EventBus
     *
     * @param context
     * @param action
     */
    private void setEventBus(Object context, boolean action) {
        if (action) {
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).register(context);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(context);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).register(context);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).unregister(context);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(context);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).unregister(context);
        }
    }


    /**
     * 刷新：获取整体状态
     */
    private void doRefresh() {
        if (loadingDialog == null) {
            return;
        }
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setLoadingText("正在更新数据");
        loadingDialog.showWithTimeout(DIALOG_TIME_DELAY);
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_CURRENT_STATER, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "获取当整体状态:" + result);
                if (state != OkHttpManager.State.SUCCESS) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(context, "操作失败：" + result);
                        }
                    });
                }
            }
        });
        //清洁度和气味有效数据是在index=1的数据帧中
        Repository.requestPitWithDelay(getActivity(), APPConstant.TOILETID, 1, 2);
    }

    /**
     * 获取昨日人流量最大值
     */
    private void getYesterdayBodyDetectorMaxValue() {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        OkHttpManager.CommonPostAsyn(WebConstant.GET_BODY_ADJUSTED_VALUE, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
                    ValueResult valueResult = new Gson().fromJson(result, ValueResult.class);
                    if (valueResult.isResult()) {
                        Log.i(TAG, "获取昨日人流量最大值：" + result);
                        APPConstant.BODY_YESTERDAY_MAX_VALUE = valueResult;
                    } else {
                        APPConstant.BODY_YESTERDAY_MAX_VALUE = null;
                    }

                }
            }
        });
    }

    /**
     * 刷新：检查toilet是否被锁
     */
    private void checkToiletLock() {
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        OkHttpManager.CommonPostAsyn(WebConstant.CHECK_TOILET, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                if (state == OkHttpManager.State.SUCCESS) {
                    LockStateResult lockedStatus = new Gson().fromJson(result, LockStateResult.class);//服务器查询得到的状态
                    lockedToilet = lockedStatus.isLocked();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lockToilet_btn.setChecked(lockedToilet);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShortToast(context, "查询失败");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        System.out.println("========onDestroy========");
        setEventBus(this, false);
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.allControlled_btn: {
                    loadingDialog.setCanceledOnTouchOutside(false);
                    loadingDialog.setLoadingText("正在更新数据");
                    loadingDialog.showWithTimeout(DIALOG_TIME_DELAY);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("toiletId", APPConstant.TOILETID);
                    params.put("action", allControlled ? "false" : "true");
                    params.put("username", APPConstant.USERNAME);
                    OkHttpManager.CommonPostAsyn(WebConstant.INTO_OR_EXIT_REMOTE_CTRL, params, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onCallBack(OkHttpManager.State state, String result) {
                            if (state != OkHttpManager.State.SUCCESS) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showShortToast(context, "操作失败：" + result);
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    if (!((Boolean) jsonObject.get("result"))) {
                                        String reason = (String) jsonObject.get("reason");
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showShortToast(context, reason);
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
                break;
                case R.id.lockToilet_btn: {
                    loadingDialog.setCanceledOnTouchOutside(false);
                    loadingDialog.setLoadingText("正在更新数据");
                    loadingDialog.show();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("toiletId", APPConstant.TOILETID);
                    OkHttpManager.CommonPostAsyn(WebConstant.CHECK_TOILET, params, new OkHttpManager.ResultCallback() {
                        @Override
                        public void onCallBack(OkHttpManager.State state, String result) {
                            loadingDialog.dismiss();
                            if (state != OkHttpManager.State.SUCCESS) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showShortToast(context, "查询失败");
                                    }
                                });
                            } else {
                                LockStateResult lockedStatus = new Gson().fromJson(result, LockStateResult.class);//服务器查询得到的状态
                                if (lockedToilet != lockedStatus.isLocked()) {
                                    lockedToilet = lockedStatus.isLocked();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ToggleButton) view).setChecked(lockedToilet);
                                            ToastUtil.showShortToast(context, "状态已更新，无需进行操作" + result);
                                        }
                                    });
                                } else {
                                    params.put("locker", APPConstant.USERNAME);
                                    String url = lockedToilet ? WebConstant.RELEASE_TOILET : WebConstant.LOCK_TOILET;
                                    OkHttpManager.CommonPostAsyn(url, params, new OkHttpManager.ResultCallback() {
                                        @Override
                                        public void onCallBack(OkHttpManager.State state, String result) {
                                            if (state != OkHttpManager.State.SUCCESS) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showShortToast(context, "操作失败" + result);
                                                    }
                                                });
                                            } else {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(result);
                                                    String reason = (String) jsonObject.get("reason");
                                                    ((Activity) context).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.showShortToast(context, reason);
                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                break;
                default:
            }
        }
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {
        if (msg.msgType == EventBusManager.MsgType.fromServer) {
            if (msg.getFlag() == 0) {
                //单个设备状态变化
                setRecordInfo(msg.getDeviceMessage());
            } else if (msg.getFlag() == 1) {
                //全体设备状态变化
                setWholeStateInfo(msg.getAllDevicesMessage());
                ToastUtil.showShortToast(context, "整体状态刷新");
            } else if (msg.getFlag() == 3) {
                //厕所锁的状态变化记录
                LockStateResult lr = msg.getLockStateResult();
                lockedToilet = lr.isLocked();
                lockToilet_btn.setChecked(lockedToilet);
            } else if (msg.getFlag() == 4) {
                //更新异味浓度和清洁度的总体百分比
                updateCleannessAndGas(msg.getBus485PitResult());
            }
            Log.d(this.getTag(), "收到来自WebSocket Service发来的信息");
        }
    }

    /**
     * 更新页面上的清洁度和气味综合值
     *
     * @param pit
     */
    private void updateCleannessAndGas(Bus485Pit pit) {
        if (pit == null) {
            return;
        }
        //坑位清洁度详情
        if (pit.getIndex() == 1) {
            clealinessLevelTv.setText(pit.cleanessLevelStr);
            gasLevelTv.setText(pit.gasLevelStr);
        }
    }


    /**
     * 单个设备的运行状态变化
     * TODO:对于 toiletId < 00001000 && deviceType=H 的单条记录，
     * 1、如果是旧版采集卡，会只收到action=true的记录
     * 2、如果是新版采集卡，toiletId >= 00001000，会收到action=true和false的记录。
     * 为了保证一致性。需要屏蔽旧版采集卡的该类型记录
     *
     * @param message
     */
    private void setRecordInfo(DeviceMessage message) {
        if (message != null) {

            //TODO  恶心的 IF
            if ("H".equals(message.getDeviceType()) && APPConstant.TOILET_VERSION == 1) {
                //旧版采集卡的 一键冲洗（H）的 单条运行记录
                return;
            }
            String name = APPConstant.deviceTypeMap.get(message.getDeviceType());
            String usage = APPConstant.UsageMap.get(message.getUsage());
            if (1 == message.getUsage()) {
                for (Devices.Device device : manDevices.getDevices()) {
                    if (device.getName().equals(name)) {
                        device.setAction(message.isAction());
                    }
                }
                manFragment.onStateChanged(manDevices);
            } else if (2 == message.getUsage()) {
                for (Devices.Device device : womanDevices.getDevices()) {
                    if (device.getName().equals(name)) {
                        device.setAction(message.isAction());
                    }
                }
                womanFragment.onStateChanged(womanDevices);
            } else if (3 == message.getUsage()) {
                for (Devices.Device device : disabledDevices.getDevices()) {
                    if (device.getName().equals(name)) {
                        device.setAction(message.isAction());
                    }
                }
                disabledFragment.onStateChanged(disabledDevices);
            } else if (4 == message.getUsage()) {
//                commonFragment.setVoiceCtrlTb1(message.isAction());
            }
        }
    }

    /**
     * 更新整体状态，对应男厕，女厕，残卫
     *
     * @param prefix：只允许M,W,D这三个值
     */
    private void setWholeStateInfoByPrefix(Devices devices, AllDevicesMessage message, String prefix) {
        Class clazz = message.getClass();
        Method method;
        for (int i = 0; i < deviceNames.length; i++) {
            try {
                if (deviceNames[i].equals("lightDetector")) {
                    method = clazz.getMethod("getValue_" + prefix + "_" + deviceNames[i]);
                    devices.getDevices().get(i).setValue((Integer) method.invoke(message));
                } else {
                    method = clazz.getMethod("isAction_" + prefix + "_" + deviceNames[i]);
                    boolean action = (Boolean) method.invoke(message);
                    devices.getDevices().get(i).setAction(action);
                    Log.i(TAG, method.getName() + " = " + action);

                    method = clazz.getMethod("isControlled_" + prefix + "_" + deviceNames[i]);
                    boolean controlled = (Boolean) method.invoke(message);
                    devices.getDevices().get(i).setControlled(controlled);
                    Log.i(TAG, method.getName() + " = " + controlled);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 更新整体状态
     *
     * @param message
     */
    private void setWholeStateInfo(AllDevicesMessage message) {
        ControlFragment.wholeStateTimestamp = System.currentTimeMillis();

        allControlled = message.isControlled();
        allCotrolled_btn.setChecked(allControlled);
        //此处的顺序应该与model.Devices中的devices List一一对应

        setWholeStateInfoByPrefix(manDevices, message, "M");
        setWholeStateInfoByPrefix(womanDevices, message, "W");
        setWholeStateInfoByPrefix(disabledDevices, message, "D");

        setWaterMeterTvValue(message.getValue_C_WaterMeter() + "");
        setElecMeterTvValue(message.getValue_C_ElecMeter() + "");
        setbodyDetectorTv(message);

        manFragment.onStateChanged(manDevices);
        womanFragment.onStateChanged(womanDevices);
        disabledFragment.onStateChanged(disabledDevices);
    }

    /**
     * 设置电表值
     *
     * @param num
     */
    public void setElecMeterTvValue(String num) {
        try {
//            this.elecMeterTvValue = num;
            System.err.println("设置电表值：" + num);
            int elecValue = Integer.parseInt(num);
            if (elecValue < 0 || elecValue > 99999999) {
                this.elecMeterTv.setText("00");
                return;
            }
            String value = String.format("%.2f", elecValue * 0.01);
            if (null != this.elecMeterTv) {
                this.elecMeterTv.setText(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("设置电表值==catch");

        }
    }

    /**
     * 设置水表值
     *
     * @param num
     */
    public void setWaterMeterTvValue(String num) {
        try {
//            this.waterMeterTvValue = num;
            System.err.println("设置水表值：" + num);
            int waterValue = Integer.parseInt(num);
            if (waterValue < 0 || waterValue > 99999999) {
                this.waterMeterTv.setText("00");
                return;
            }
            String value = String.format("%.2f", (waterValue * 0.01));
            if (null != this.waterMeterTv) {
                this.waterMeterTv.setText(value.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("设置水表值===catch");

        }
    }

    /**
     * 调用服务器接口，获取昨天的最大人流量。然后做减法，显示当天人流量
     * 设置人流量值
     */
    public void setbodyDetectorTv(AllDevicesMessage message) {
        int num = message.getValue_D_bodyDetector();
        num += message.getValue_W_bodyDetector();
        num += message.getValue_M_bodyDetector();
        int temp = num;
        if (APPConstant.BODY_YESTERDAY_MAX_VALUE != null
                && APPConstant.BODY_YESTERDAY_MAX_VALUE.getToiletId().equals(APPConstant.TOILETID)) {
            num -= APPConstant.BODY_YESTERDAY_MAX_VALUE.getValue();
        }
        bodyDetectorTv.setText((num >= 0 ? num : temp) / 2 + "");
    }

    @Override
    public boolean isControlled() {
        return allControlled;
    }

    public boolean isLockedToilet() {
        return lockedToilet;
    }
}
