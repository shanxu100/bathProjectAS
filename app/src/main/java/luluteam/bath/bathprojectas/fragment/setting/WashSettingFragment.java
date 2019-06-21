package luluteam.bath.bathprojectas.fragment.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.databinding.FragmentWashSettingBinding;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.main.ManageSetFragment;
import luluteam.bath.bathprojectas.fragment.wash.WashFragment;
import luluteam.bath.bathprojectas.model.wash.WashParams;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;

/**
 * 冲水分段参数 父fragment
 */
public class WashSettingFragment extends BaseFragment implements ManageSetFragment.WashParamsCallback {
    private FragmentWashSettingBinding mBinding;
    private List<String> titleList = new ArrayList<>();
    private List<WashFragment> fragmentList = new ArrayList<>();
    private FragmentManager manager;

    private WashFragment manWashFragment;
    private WashFragment womanWashFragment;
    private WashFragment disableWashFragment;

    private String lastToiletId = "";
    private static Handler initRequestHandler = null;

    public static final String TAG = "WashSettingFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEventBus(this, true);
        Log.i(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wash_setting, container, false);
        init();
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
//        Log.e(TAG,"父 fragment onStart()=================");
        super.onStart();
        if (!lastToiletId.equals(APPConstant.TOILETID)) {
            //TODO 更改ToiletID后，要根据新的 itemCommon 刷新UI
            for (WashFragment fragment : fragmentList) {
                fragment.UpdateGridUI(getContext());
            }
            lastToiletId = APPConstant.TOILETID;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setEventBus(this, false);
        if (initRequestHandler != null) {
            initRequestHandler.removeCallbacksAndMessages(null);
            initRequestHandler = null;
        }

    }

    /* （取消）注册 EventBus
     *
     * @param context
     * @param action
     */
    private void setEventBus(Object context, boolean action) {
        if (action) {

            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).register(context);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).unregister(context);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {
        if (msg.msgType == EventBusManager.MsgType.fromServer) {
            if (msg.getFlag() == 4) {
                //得先判断是否是index=0的公共数据
                if (msg.getBus485PitResult().getIndex() == 0) {
                    ToastUtil.showShortToast(getContext(), "更新公共数据部分成功。。。");
                    APPConstant.itemCommon = msg.getBus485PitResult().itemCommon;
                    //更新子fragment UI
                    for (WashFragment fragment : fragmentList) {
                        fragment.UpdateGridUI(getContext());
                    }
                }

            }
            Log.d(TAG, "收到来自WebSocket Service发来的信息");
        }
    }

    /**
     * 处理参数
     *
     * @param washParams
     */
    private void onWashParams(WashParams washParams) {
//        int frameIndex = washParams.getFrameIndex();
        String usage = washParams.getUsage();
        if ("1".equals(usage)) {
            manWashFragment.onPitWashSetReceived(washParams.getList().subList(0, 60), 0);
            manWashFragment.onRoadWashSetReceived(washParams.getList().subList(60, 70), 0);
            ToastUtil.showShortToast(getContext(), "更新冲水设备参数数据：usage = " + usage);
        } else if ("2".equals(usage)) {
            womanWashFragment.onPitWashSetReceived(washParams.getList().subList(0, 60), 0);
            womanWashFragment.onRoadWashSetReceived(washParams.getList().subList(60, 70), 0);
            ToastUtil.showShortToast(getContext(), "更新冲水设备参数数据：usage = " + usage);
        } else if ("3".equals(usage)) {
            disableWashFragment.onPitWashSetReceived(washParams.getList().subList(0, 10), 0);
            disableWashFragment.onRoadWashSetReceived(washParams.getList().subList(10, 20), 0);
            ToastUtil.showShortToast(getContext(), "更新冲水设备参数数据：usage = " + usage);
        } else {
            ToastUtil.showShortToast(getContext(), "更新冲水设备参数数据：usage = " + usage);
        }
    }

    /**
     * 请求参数
     *
     * @param frameIndex
     */
    public void refreshWashParams(int frameIndex) {
        /**
         * frameIndex 见 {@Link WashParams.java} 的描述
         */
        HashMap<String, String> params = new HashMap<>();
        params.put("toiletId", APPConstant.TOILETID);
        if (frameIndex != -1) {
            params.put("frameIndex", frameIndex + "");
        }
        OkHttpManager.CommonPostAsyn(WebConstant.REQUEST_WASH_PARAMS, params, new OkHttpManager.ResultCallback() {
            @Override
            public void onCallBack(OkHttpManager.State state, String result) {
                Log.i(TAG, "请求冲水设备参数数据:" + WebConstant.REQUEST_WASH_PARAMS + " params:" + params.toString());
                if (state != OkHttpManager.State.SUCCESS) {
                    ToastUtil.logAndToast(getContext(), "发送失败：" + result);
                }
            }
        });

    }

    public void refreshWashParams(int frameIndex, long delay) {
        if (initRequestHandler == null) {
            initRequestHandler = new Handler();
        }
        initRequestHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshWashParams(frameIndex);
            }
        }, delay);
    }

    /**
     * 请求数量，初始化页面
     */
    private void init() {
        ToastUtil.showShortToast(getContext(), "刷新冲水参数设置页面");
        titleList.clear();
        titleList.add("男厕");
        titleList.add("女厕");
        titleList.add("其他");
        manager = getChildFragmentManager();
        initFragments();


    }

    private void initFragments() {

        manWashFragment = WashFragment.getWashFragment(WashFragment.USAGE_MAN);
        womanWashFragment = WashFragment.getWashFragment(WashFragment.USAGE_WOMAN);
        disableWashFragment = WashFragment.getWashFragment(WashFragment.USAGE_DISABLED);
        fragmentList.clear();
        fragmentList.add(manWashFragment);
        fragmentList.add(womanWashFragment);
        fragmentList.add(disableWashFragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        };

        mBinding.viewPager.setAdapter(fragmentPagerAdapter);
        //让ViewPager多缓存一个页面,防止切换的切换tab的时候，使得fragment重新创建
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.tablayout.setupWithViewPager(mBinding.viewPager);
//                mBinding.tablayout.getTabAt(getIntent().getIntExtra(EXTRA_NAME_SELECTED_TAB, 1)).select();


    }

    @Override
    public void setWashParamsSocket(WashParams washParams) {
        onWashParams(washParams);
    }
}
