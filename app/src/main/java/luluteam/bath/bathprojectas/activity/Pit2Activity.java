package luluteam.bath.bathprojectas.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import luluteam.bath.bathprojectas.databinding.ActivityPitBinding;
import luluteam.bath.bathprojectas.fragment.pit.PitFragment2;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.tools.Repository;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.view.dialog.LoadingDialog;

public class Pit2Activity extends BaseActivity {

    private ActivityPitBinding mBinding;
    private FragmentManager fragmentManager;


    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private PitFragment2 manPitFragment;
    private PitFragment2 womanPitFragment;
    private PitFragment2 disabledPitFragment;

    public static final String EXTRA_NAME_TOILETID = "toiletId";
    public static final String EXTRA_NAME_SELECTED_TAB = "selectedTab";

//    public static Bus485Pit.ItemCommon itemCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pit);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_pit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
        } else if (item.getItemId() == R.id.action_pit_refresh) {
            refreshPit(1);
        } else if (item.getItemId() == R.id.action_0_refresh) {
            refreshPit(0);
            ToastUtil.showLongToast(context, "退出并重新进入该页面后，显示最新设备数量");
        } else if (item.getItemId() == R.id.action_2_refresh) {
            refreshPit(2);
        } else if (item.getItemId() == R.id.action_3_refresh) {
            refreshPit(3);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setEventBus(this, false);
    }

    private void init() {
        setupToolbar();
        setEventBus(this, true);
//        mBinding.setToiletInfo(APPConstant.TOILETID + APPConstant.DETAIL);
        mBinding.setToiletInfo(APPConstant.TOILETID + "  v:" + APPConstant.TOILET_VERSION + "    " + APPConstant.TOILET_NICKNAME);
        fragmentManager = getSupportFragmentManager();
        titleList.clear();
        titleList.add("男厕");
        titleList.add("女厕");
        titleList.add("第三卫生间");
        Repository.getItemCommon(this, APPConstant.TOILETID, new Repository.DataCallback() {
            @Override
            public void onData(Object... args) {
                initFragments();
                refreshPit(1);
            }
        });

    }

    private void initFragments() {
        manPitFragment = PitFragment2.getPitFragment(PitFragment2.USAGE_MAN);
        womanPitFragment = PitFragment2.getPitFragment(PitFragment2.USAGE_WOMAN);
        disabledPitFragment = PitFragment2.getPitFragment(PitFragment2.USAGE_DISABLED);
        fragmentList.clear();
        fragmentList.add(manPitFragment);
        fragmentList.add(womanPitFragment);
        fragmentList.add(disabledPitFragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(fragmentManager) {
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
        mBinding.tablayout.getTabAt(getIntent().getIntExtra(EXTRA_NAME_SELECTED_TAB, 1)).select();

    }

    private void setupToolbar() {
        ((Toolbar) mBinding.toolbar).setTitle("详情");
        this.setSupportActionBar((Toolbar) mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void refreshPit(int index) {

        /**
         * index 见 {@Link Bus485Pit.java} 的描述
         */
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.showWithTimeout(2000);
        Repository.requestPit(this, APPConstant.TOILETID, index);

    }

    /**
     * （取消）注册 EventBus
     *
     * @param context
     * @param action
     */
    private void setEventBus(Object context, boolean action) {
        if (action) {
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).register(context);
            }
            if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(context)) {
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(context);
            }
        } else {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).unregister(context);
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(context);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {

        if (msg.msgType == EventBusManager.MsgType.fromServer) {
            if (msg.getFlag() == 4) {
                //蹲位数据
                onToiletPit(msg.getBus485PitResult());
            }
            Log.d(TAG, "收到来自WebSocket Service发来的信息");
        }

    }

    /**
     * 向fragment发送蹲位数据
     *
     * @param pit
     */
    public void onToiletPit(Bus485Pit pit) {
        ToastUtil.showShortToast(context, "更新蹲位数据：index = " + pit.getIndex());

        if (pit.getIndex() == 0) {
            /**
             * 16字节公共数据，用来初始化显示的图标的数量
             */
//            this.itemCommon = pit.itemCommon;
            APPConstant.itemCommon = pit.itemCommon;
        } else if (pit.getIndex() == 1) {
            /**
             * 更新三个fragment中的图标显示状态
             */
            manPitFragment.onPitData(pit.manList);
            manPitFragment.onWaterData(pit.manWashList);
            manPitFragment.onGasData(pit.manGasList);

            womanPitFragment.onPitData(pit.womanList);
            womanPitFragment.onWaterData(pit.womanWashList);
            womanPitFragment.onGasData(pit.womanGasList);

            disabledPitFragment.onPitData(pit.disabledList);
            disabledPitFragment.onWaterData(pit.disabledWashList);
            disabledPitFragment.onGasData(pit.disabledGasList);
        } else if (pit.getIndex() == 2) {
            manPitFragment.onPitDetailData(pit.manDetailList);
            womanPitFragment.onPitDetailData(pit.womanDetailList);
            disabledPitFragment.onPitDetailData(pit.disabledDetailList);
        } else if (pit.getIndex() == 3) {
            manPitFragment.onGasDetailData(pit.manGasDetailList);
            womanPitFragment.onGasDetailData(pit.womanGasDetailList);
            disabledPitFragment.onGasDetailData(pit.disabledGasDetailList);
        } else {
            Log.e(TAG, "index值错误，不予进行解析显示");
            return;
        }


    }

    private void onBack() {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("退出蹲位、清洁度详情页面吗？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }


    @Override
    public void onBackPressed() {
        onBack();
    }

    public interface PitDataListener {

        /**
         * 对界面进行初始化设置，以及界面的简单更新，不包括冲水或者气体的详细数据
         */
        void onPitData(List<Bus485Pit.Item> itemList);

        void onWaterData(List<Bus485Pit.Item> itemList);

        void onGasData(List<Bus485Pit.Item> itemList);

        /**
         * 查看特定的气味探测器的详细数据
         */
        void onGasDetailData(List<Bus485Pit.ItemGas> itemGasList);

        /**
         * 查看特定的坑位的详细数据
         */
        void onPitDetailData(List<Bus485Pit.ItemPit> itemPitList);

    }
}
