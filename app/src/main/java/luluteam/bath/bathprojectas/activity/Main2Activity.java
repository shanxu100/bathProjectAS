package luluteam.bath.bathprojectas.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.fragment.BaseFragment;
import luluteam.bath.bathprojectas.fragment.main.ControlFragment;
import luluteam.bath.bathprojectas.fragment.main.MainFragment;
import luluteam.bath.bathprojectas.fragment.main.ManageSetFragment;
import luluteam.bath.bathprojectas.fragment.main.StatisticFragment;
import luluteam.bath.bathprojectas.fragment.statistics.IChangeToiletTypeCallback;
import luluteam.bath.bathprojectas.fragment.statistics.IStatisticFragment;
import luluteam.bath.bathprojectas.model.ToiletInfo;
import luluteam.bath.bathprojectas.services.UpdateService;
import luluteam.bath.bathprojectas.services.floatwindow.FloatWindowSimpleService;
import luluteam.bath.bathprojectas.services.mqtt.MqttService;
import luluteam.bath.bathprojectas.services.websocket.WebSocketClient;
import luluteam.bath.bathprojectas.services.websocket.WebSocketService;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.tools.JumpHelper;
import luluteam.bath.bathprojectas.utils.ClickUtil;
import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.utils.okhttp.OkHttpManager;
import luluteam.bath.bathprojectas.view.MultiToiletPopWin;
import luluteam.bath.bathprojectas.view.TabItemView;
import luluteam.bath.bathprojectas.view.dialog.ChooseUsageDialog;


public class Main2Activity extends BaseActivity implements BaseFragment.InfoCallback {

    //    private Spinner mSpinner;
    private ImageView functioniv, meiv;
    private TextView functiontv, metv, nowToiletId_tv, toiletId_str_tv, usage_tv;

    //这里要给Fragment内部调用---懒得写了
    public TabLayout buttom_tablayout;

    private Toolbar include_toolbar;
    private RelativeLayout toiletInfo_rl;


    private FragmentTransaction transaction;
    private FragmentManager manager;
    private StatisticFragment statisticFragment;

    private DrawerLayout drawerLayout;

    private Intent websocketIntent;
    private Intent mqttIntent;
    private Intent networkStateIntent;
    private Intent updateIntent;

    private LinearLayout websocketstate_ll, mqttstate_ll;
    private MultiToiletPopWin multiToilet_popwin;

    private List<IChangeToiletTypeCallback> mCallbackList;
    private IStatisticFragment iStatisticFragment;

    List<TabItemView.ItemHolder> itemHolderList = new ArrayList<>();

    private MainFragment mainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        manager = getSupportFragmentManager();
        initView();
        initData();

        if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(this)) {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(this);
        }

        showToiletInfo(APPConstant.TOILETID, APPConstant.DETAIL, APPConstant.TOILET_VERSION, APPConstant.TOILET_NICKNAME);
        showToiletUsage();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mainFragment.onNewToiletItem(data);
        }
    }

    /**
     * 初始化
     */
    private void initView() {

        functioniv = (ImageView) findViewById(R.id.functioniv);
        meiv = (ImageView) findViewById(R.id.meiv);

        functiontv = (TextView) findViewById(R.id.functiontv);
        metv = (TextView) findViewById(R.id.metv);
        usage_tv = (TextView) findViewById(R.id.usage_tv);
        toiletInfo_rl = (RelativeLayout) findViewById(R.id.toiletInfo_rl);

        buttom_tablayout = (TabLayout) findViewById(R.id.buttom_tablayout);
        websocketstate_ll = (LinearLayout) findViewById(R.id.websocketstate_ll);
        mqttstate_ll = (LinearLayout) findViewById(R.id.mqttstate_ll);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        include_toolbar = (Toolbar) findViewById(R.id.include_toolbar);

        nowToiletId_tv = (TextView) findViewById(R.id.nowToiletId_tv);
        toiletId_str_tv = (TextView) findViewById(R.id.toiletId_str_tv);
        multiToilet_popwin = new MultiToiletPopWin(this);

        mCallbackList = new ArrayList<>();
        setActionBar();

        toiletInfo_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseUsageDialog dialog = new ChooseUsageDialog(context);
                dialog.show();
                dialog.setClickSureBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String usage = dialog.getCheckedUsage();
                        SharedPreferencesUtil.putString(context, "usage", usage);
                        APPConstant.USAG = usage;
                        dialog.dismiss();
                        showToiletUsage();
                        //在切换厕所类型的时候，及时更新统计分析中的数据
                        for (IChangeToiletTypeCallback callback : mCallbackList) {
                            callback.onChanged();
                        }

                    }
                });
            }
        });
    }

    public void setChangeToiletTypeCallback(IChangeToiletTypeCallback callback) {
        this.mCallbackList.add(callback);
    }

    private void initData() {
        setupFragment();
        /**
         * 开启各种service
         */
        websocketIntent = new Intent(this, WebSocketService.class);
        mqttIntent = new Intent(this, MqttService.class);
        networkStateIntent = new Intent(this, FloatWindowSimpleService.class);
        updateIntent = new Intent(this, UpdateService.class);
        startService(websocketIntent);
        startService(mqttIntent);
        startService(networkStateIntent);
//        startService(updateIntent);
        checkPermissions();
        checkUpdate();

    }

    private void setupFragment() {
        statisticFragment = new StatisticFragment();
        mainFragment = new MainFragment();
        /**
         * 初始化多组Item数据，供Tab使用：一个Tab Fragment和多个Blank Fragment
         */
        itemHolderList.add(new TabItemView.ItemHolder("Tag1", mainFragment,
                new TabItemView(context, "首页", 0, R.drawable.selector_tab_overview, 0)));
        itemHolderList.add(new TabItemView.ItemHolder("Tag2", new ControlFragment(),
                new TabItemView(context, "远程控制", 0, R.drawable.selector_tab_remote_control, 0)));
        itemHolderList.add(new TabItemView.ItemHolder("Tag3", statisticFragment,
                new TabItemView(context, "统计分析", 0, R.drawable.selector_tab_stats, 0)));
        itemHolderList.add(new TabItemView.ItemHolder("Tag4", new ManageSetFragment(),
                new TabItemView(context, "设备管理", 0, R.drawable.selector_tab_setting, 0)));


        /**
         * Tab 和 具体的数据ItemHolder 绑定
         */
        buttom_tablayout.addTab(buttom_tablayout.newTab().setCustomView(itemHolderList.get(0).tabItemView), 0, true);//默认第一个item被选中
        buttom_tablayout.addTab(buttom_tablayout.newTab().setCustomView(itemHolderList.get(1).tabItemView), 1, false);
        buttom_tablayout.addTab(buttom_tablayout.newTab().setCustomView(itemHolderList.get(2).tabItemView), 2, false);
        buttom_tablayout.addTab(buttom_tablayout.newTab().setCustomView(itemHolderList.get(3).tabItemView), 3, false);

        buttom_tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            private int needHidePosition;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabSelected:" + tab.getTag() + "\t" + tab.getPosition());
                changeFragment(tab.getPosition(), needHidePosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabUnselected:" + tab.getTag() + "\t" + tab.getPosition());
                needHidePosition = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabReselected:" + tab.getTag() + "\t" + tab.getPosition());
                if (tab.getPosition() == 1) {
                    multiToilet_popwin.setWidth(tab.getCustomView().getWidth());
                    multiToilet_popwin.setHeight(tab.getCustomView().getWidth() * 2);
                    multiToilet_popwin.show(tab.getCustomView());
                }
            }
        });

        //初始化第一个fragment
        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.mainfrag, itemHolderList.get(0).fragment, itemHolderList.get(0).fragmentTag)
                .show(itemHolderList.get(0).fragment)
                .commit();
    }

    @Deprecated
    public void setSelect() {
        buttom_tablayout.getTabAt(2).select();
        statisticFragment.setSelect();
    }

    /**
     * 设置ActionBar的样式，以及添加的一些功能
     */
    private void setActionBar() {
        include_toolbar.setTitle("首页");
        this.setSupportActionBar(include_toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * 加载toiletId
     *
     * @param toiletId
     */
    private void showToiletInfo(String toiletId, String detail, int toiletVersion, String toiletNickname) {
        if (StringUtils.isEmpty(toiletId)) {
            nowToiletId_tv.setText("请选择 ToiletId");
            toiletId_str_tv.setText("");
        } else {
            APPConstant.TOILETID = toiletId;
            APPConstant.DETAIL = detail;
            APPConstant.TOILET_VERSION = toiletVersion;
            APPConstant.TOILET_NICKNAME = toiletNickname;
            SharedPreferencesUtil.putString(context, APPConstant.USERNAME + "_toiletId", APPConstant.TOILETID);
            SharedPreferencesUtil.putString(context, APPConstant.USERNAME + "_detail", APPConstant.DETAIL);
            SharedPreferencesUtil.putString(context, APPConstant.USERNAME + "_toiletVersion", APPConstant.TOILET_VERSION + "");
            SharedPreferencesUtil.putString(context, APPConstant.USERNAME + "_toiletNickname", APPConstant.TOILET_NICKNAME);

            WebSocketClient.getInstance().sendMsg(APPConstant.TOILETID);
//            nowToiletId_tv.setText(APPConstant.TOILETID + " v:" + APPConstant.TOILET_VERSION+" ");
            nowToiletId_tv.setText(APPConstant.TOILETID + " ");
//            toiletId_str_tv.setText(APPConstant.DETAIL);
            toiletId_str_tv.setText(APPConstant.TOILET_NICKNAME);
            ((ControlFragment) itemHolderList.get(1).fragment).onReloadImage();
            ((ControlFragment) itemHolderList.get(1).fragment).onNewToilet();
        }
    }

    /**
     * 显示当前厕所ID
     */
    private void showToiletUsage() {
        switch (APPConstant.USAG) {
            case "1":
                usage_tv.setText("男厕");
                break;
            case "2":
                usage_tv.setText("女厕");
                break;
            case "3":
                usage_tv.setText("残卫");
                break;
            default:
                usage_tv.setText(APPConstant.USAG);
        }
    }

    /**
     * fragment的切换
     *
     * @param showPosition
     * @param hidePosition
     */
    private void changeFragment(int showPosition, int hidePosition) {
        transaction = manager.beginTransaction();
        Fragment showFragment = manager.findFragmentByTag(itemHolderList.get(showPosition).fragmentTag);
        if (showFragment == null) {
            transaction.add(R.id.mainfrag,
                    itemHolderList.get(showPosition).fragment,
                    itemHolderList.get(showPosition).fragmentTag);
        }
        transaction.hide((itemHolderList.get(hidePosition).fragment))
                .show((itemHolderList.get(showPosition).fragment))
                .commit();
        include_toolbar.setTitle(itemHolderList.get(showPosition).tabItemView.title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exitApp:
                exitApp();
                break;
            case R.id.action_overview:
                JumpHelper.gotoWebActivity(context, "http://www.claudy.com.cn/comcontent_detail.html");
                break;
            case R.id.action_change_pwd:
                ToastUtil.showLongToast(context, "action_change_pwd");
                break;
            case R.id.action_video:
                JumpHelper.gotoVideoActivity(context, null);
                break;
            case R.id.action_about:
                JumpHelper.gotoAboutActivity(context);
                break;
            case android.R.id.home:
                buttom_tablayout.getTabAt(0).select();
                break;
            default:
                break;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {

    }


    /**
     * 检查权限
     */
    private void checkPermissions() {
        Intent permissionIntent = new Intent(this, PermissionActivity.class);
        startActivity(permissionIntent);
    }

    private void checkAndMakeDir() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            return;
        }

    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
//        Intent intent = new Intent(this, UpdateActivity.class);
//        startActivity(intent);
        JumpHelper.gotoUpdateActivity(context);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(this);
        stopService(websocketIntent);
        stopService(mqttIntent);
        stopService(networkStateIntent);
//        stopService(updateIntent);

    }

    /**
     * 退出Activity
     */
    @Override
    public void onBackPressed() {
        if (ClickUtil.isFastDoubleClick()) {
            moveTaskToBack(true);
        } else {
            ToastUtil.showShortToast(context, "再按一次程序返回后台运行");
        }
    }


    @Override
    public void onToiletItem(ToiletInfo.ToiletItem item) {
        showToiletInfo(item.getToiletId(), item.getDetail(), item.getVersion(), item.getNickname());
        buttom_tablayout.getTabAt(1).select();
        ToastUtil.showLongToast(context, "设置Toilet成功：" + item.getToiletId());

    }


    /**
     * 点击退出按钮
     */
    private void exitApp() {

        new AlertDialog.Builder(context).setMessage("您要退出该账户吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 通知服务器,user退出
                         */
                        HashMap<String, String> params = new HashMap<>();
                        params.put("username", APPConstant.USERNAME);
                        OkHttpManager.CommonPostAsyn(WebConstant.LOGOUT, params, new OkHttpManager.ResultCallback() {
                            @Override
                            public void onCallBack(OkHttpManager.State state, String result) {

                            }
                        });
                        App.Logout();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();


    }

}
