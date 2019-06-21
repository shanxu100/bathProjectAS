package luluteam.bath.bathprojectas.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.fragment.rundevice.RunDeviceDetailFragment;
import luluteam.bath.bathprojectas.model.AllDeviceWorkInfo;
import luluteam.bath.bathprojectas.model.RunDeviceDetail;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.view.TabItemViewCtrlFragment;

/**
 * DeviceState
 * 显示设备运行状态详情
 *
 * @author
 */
public class DeviceRunDetailActivity extends BaseActivity {

    public static final String MAN = "man";
    public static final String WOMAN = "woman";
    public static final String THIRD = "third";


    private RunDeviceDetailFragment manFragment;
    private RunDeviceDetailFragment womanFragment;
    private RunDeviceDetailFragment thirdFragment;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private List<RunDeviceDetailFragment> fragmentList;
    private List<TabItemViewCtrlFragment> tabItemViewList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_run_detail);
        initView();
        initData();
        setEventBus(this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setEventBus(this, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMsg(EventBusManager.EventBusMsg msg) {
        AllDeviceWorkInfo workInfo = msg.getAllDeviceWorkInfo();
        if (workInfo != null && APPConstant.TOILETID.equals(workInfo.getToiletId())) {
            Log.i(TAG, workInfo.toString());
            manFragment.refreshData(AllDeviceWorkInfo.TO_DETAIL(workInfo.getManDeviceValue(), workInfo.getCommonDeviceValue()));
            womanFragment.refreshData(AllDeviceWorkInfo.TO_DETAIL(workInfo.getWomanDeviceValue(), workInfo.getCommonDeviceValue()));
            thirdFragment.refreshData(AllDeviceWorkInfo.TO_DETAIL(workInfo.getDisableDeviceValue(), workInfo.getCommonDeviceValue()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_device_state, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_device_state_refresh:
                refreshDeviceState();
                break;
            case android.R.id.home:
                this.finish();
            default:
                break;
        }
        return true;
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.run_device_detail_tab);
        viewPager = (ViewPager) findViewById(R.id.container);
        toolbar = (Toolbar) findViewById(R.id.include_toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle("设备状态详情");
        getSupportActionBar().setTitle("设备状态详情");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initData() {
        fragmentList = new ArrayList<>();
        tabItemViewList = new ArrayList<>();
        manFragment = new RunDeviceDetailFragment();
        womanFragment = new RunDeviceDetailFragment();
        thirdFragment = new RunDeviceDetailFragment();

        List<RunDeviceDetail> manDetail = (List<RunDeviceDetail>) getIntent().getSerializableExtra(MAN);
        List<RunDeviceDetail> womanDetail = (List<RunDeviceDetail>) getIntent().getSerializableExtra(WOMAN);
        List<RunDeviceDetail> thirdDetail = (List<RunDeviceDetail>) getIntent().getSerializableExtra(THIRD);

        manFragment.setDeviceAdapter(new RunDeviceDetailFragment.RunDeviceAdapter(this, R.layout.run_device_item, manDetail));
        womanFragment.setDeviceAdapter(new RunDeviceDetailFragment.RunDeviceAdapter(this, R.layout.run_device_item, womanDetail));
        thirdFragment.setDeviceAdapter(new RunDeviceDetailFragment.RunDeviceAdapter(this, R.layout.run_device_item, thirdDetail));

        fragmentList.add(manFragment);
        fragmentList.add(womanFragment);
        fragmentList.add(thirdFragment);
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_man_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_woman_selector, 0));
        tabItemViewList.add(new TabItemViewCtrlFragment(this, R.drawable.tab_item_third_selector, 0));

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(1).select();

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //手动指定TabLayout中Item的View
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(tabItemViewList.get(i));
        }
    }

    /**
     * （取消）注册 EventBus
     *
     * @param context
     * @param action
     */
    private void setEventBus(Object context, boolean action) {
        if (action) {
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).register(context);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(context);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).unregister(context);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(context);
        }
    }


    private void refreshDeviceState() {
        Repository.refreshDeviceState(context, APPConstant.TOILETID);
    }
}
