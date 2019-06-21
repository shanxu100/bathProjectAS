package luluteam.bath.bathprojectas.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.setting.AutoWashTimer2Fragment;
import luluteam.bath.bathprojectas.fragment.setting.DeviceSetting2Fragment;
import luluteam.bath.bathprojectas.fragment.setting.WashSettingFragment;
import luluteam.bath.bathprojectas.model.AutoWashTimer2Info;
import luluteam.bath.bathprojectas.model.AutoWashTimerInfo;
import luluteam.bath.bathprojectas.model.ParamsInfo;
import luluteam.bath.bathprojectas.model.wash.WashParams;
import luluteam.bath.bathprojectas.tools.EventBusManager;

/**
 * Created by Administrator on 2018/3/16 0016.
 */

public class ManageSetFragment extends BaseFragment {

    private FragmentManager fragmentManager;
    private ViewPager manageSet_viewpager;
    private TabLayout manageSet_tablayout;


    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    //    private DeviceSetFragment deviceSetFragment;
//    private DeviceSettingFragment deviceSettingFragment;
//    private UserManageFragment userManageFragment;
//    private ToiletManageFragment toiletManageFragment;
    private DeviceSetting2Fragment deviceSetting2Fragment;
    private WashSettingFragment washSettingFragment;
    //    private AutoWashTimerFragment autoWashTimerFragment;
    private AutoWashTimer2Fragment autoWashTimerFragment;
    /**
     * 用于存储websocket传来的数据，分为男厕，女厕，殘卫三种情况存储
     */
    private HashMap<String, HashMap<String, String>> params;
    /**
     * websocket 的采集卡的系统时间
     */
    private String time;

    public ManageSetFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_set, container, false);
        initUI(view);
        initData();
        return view;
    }

    private void initUI(View view) {
        manageSet_tablayout = (TabLayout) view.findViewById(R.id.manageSet_tab);
        manageSet_viewpager = (ViewPager) view.findViewById(R.id.container);

    }

    private void initData() {
        setEventBus(true);

        //嵌套要用这个函数拿到 FragmentManager
        fragmentManager = getChildFragmentManager();

//        deviceSettingFragment = new DeviceSettingFragment();
//        toiletManageFragment = new ToiletManageFragment();
//        userManageFragment = new UserManageFragment();
        deviceSetting2Fragment = new DeviceSetting2Fragment();
        washSettingFragment = new WashSettingFragment();
        autoWashTimerFragment = new AutoWashTimer2Fragment();

        fragmentList.add(deviceSetting2Fragment);
        fragmentList.add(autoWashTimerFragment);
        fragmentList.add(washSettingFragment);

        titleList.add("设备设置");
        titleList.add("冲水定时器");
        titleList.add("冲水分段参数");


        manageSet_viewpager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
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
        });
        manageSet_viewpager.setOffscreenPageLimit(2);
        manageSet_tablayout.setupWithViewPager(manageSet_viewpager);
    }


    /**
     * （取消）注册 EventBus
     *
     * @param action
     */
    private void setEventBus(boolean action) {
        if (action) {
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Params).isRegistered(this)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Params).register(this);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(this)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(this);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.autoWash).isRegistered(this)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.autoWash).register(this);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.wash).isRegistered(this)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.wash).register(this);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Params).unregister(this);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(this);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.autoWash).unregister(this);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.wash).unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {

        if (msg.msgType == EventBusManager.MsgType.fromServer) {
            if (msg.getFlag() == 2) {
                System.out.println("flag=2,进行管理设置相关参数设置！");
//                deviceSettingFragment.setParamsBySocket(msg.getParamsInfo());
                deviceSetting2Fragment.setParamsBySocket(msg.getParamsInfo());
            }
            if (msg.getFlag() == 5) {
                System.out.println("flag=5,进行冲水设置相关参数设置！");
//                deviceSettingFragment.setParamsBySocket(msg.getParamsInfo());
                washSettingFragment.setWashParamsSocket(msg.getWashParams());
            }

            if (msg.getFlag() == 6) {
                System.out.println("flag=6,进行自动冲水定时器参数设置！");
//                autoWashTimerFragment.setAutoWashTimerSocket(msg.getAutoWashTimerInfo());
                autoWashTimerFragment.setAutoWashTimerSocket(msg.getAutoWashTimer2Info());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        setEventBus(true);
    }

    /**
     * 通知 Params 数据发生了改变
     */
    public interface ParamsCallback {
        void setParamsBySocket(ParamsInfo paramsInfo);
    }

    public interface AutoWashTimerCallback {
        void setAutoWashTimerSocket(AutoWashTimerInfo info);
    }

    public interface AutoWashTimer2Callback {
        void setAutoWashTimerSocket(AutoWashTimer2Info info);
    }

    public interface WashParamsCallback {
        void setWashParamsSocket(WashParams washParams);
    }
}
