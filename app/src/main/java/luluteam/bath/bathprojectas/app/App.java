package luluteam.bath.bathprojectas.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.services.mqtt.MqttSender;
import luluteam.bath.bathprojectas.tools.JumpHelper;
import luluteam.bath.bathprojectas.utils.DisplayUtil;
import luluteam.bath.bathprojectas.utils.SharedPreferencesUtil;
import luluteam.bath.bathprojectas.utils.crash.MyCrashHandler2;


/**
 * Created by TC on 2017/12/8.
 */

public class App extends Application {

    private static Context appContext;
    private static App mInstance;
    //主线程处理handler
    private static Handler mHandler;

    /**
     * 获取实例
     *
     * @return
     */
    public static App getInstance() {

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        mInstance = this;
        mHandler = new Handler();
        init();
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }


    private void init() {
//        String toiletId = SharedPreferencesUtil.getString(this, "toiletId");
        String usage = SharedPreferencesUtil.getString(this, "usage");
        String province = SharedPreferencesUtil.getString(this, "province");
        String city = SharedPreferencesUtil.getString(this, "city");
        String county = SharedPreferencesUtil.getString(this, "county");
//        String detail = SharedPreferencesUtil.getString(this, "detail");
//        APPConstant.TOILETID = (StringUtils.isEmpty(toiletId)) ? "" : toiletId;
        APPConstant.USAG = StringUtils.isEmpty(usage) ? "点击设置" : usage;
        APPConstant.PROVIENCE = (StringUtils.isEmpty(province)) ? "" : province;
        APPConstant.CITY = (StringUtils.isEmpty(city)) ? "" : city;
        APPConstant.COUNTY = (StringUtils.isEmpty(county)) ? "" : county;
//        APPConstant.DETAIL = (StringUtils.isEmpty(detail)) ? "" : detail;


        /**
         * 设置屏幕的宽度和高度
         */
        DisplayUtil.init(getBaseContext());

        /**
         * 获取AndroidId，用于标识设备
         */
        APPConstant.ANDROID_ID = Settings.Secure.getString(App.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Thread.setDefaultUncaughtExceptionHandler(new MyCrashHandler2(getAppContext()));


    }

    public static Context getAppContext() {
        return appContext;
    }

    /**
     * 版本号
     *
     * @return
     */
    public static int getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            PackageManager pm = appContext.getPackageManager();
            packageInfo = pm.getPackageInfo(appContext.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取版本名
     *
     * @return
     */
    public static String getVersionName()//获取版本号
    {
        try {
            PackageInfo pi = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "unknown version name";
        }
    }

    /**
     * 判断app是否处于前台
     *
     * @return
     */
    public boolean isAppForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(getPackageName()) &&
                    processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注销
     */
    public static void Logout() {
        /**
         * 此处表明已经收到 单点登录退出app的遗言，需要发送新的 空消息 删除该遗言
         */
        MqttSender.sendMessage(APPConstant.ANDROID_ID, "", true);
        JumpHelper.gotoLoginActivity(appContext, true);
    }

}
