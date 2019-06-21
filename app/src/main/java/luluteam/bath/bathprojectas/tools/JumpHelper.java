package luluteam.bath.bathprojectas.tools;

import android.content.Context;
import android.content.Intent;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

import luluteam.bath.bathprojectas.activity.AboutActivity;
import luluteam.bath.bathprojectas.activity.DeviceRunDetailActivity;
import luluteam.bath.bathprojectas.activity.DisplayFragmentActivity;
import luluteam.bath.bathprojectas.activity.Login2Activity;
import luluteam.bath.bathprojectas.activity.Pit2Activity;
import luluteam.bath.bathprojectas.activity.UpdateActivity;
import luluteam.bath.bathprojectas.activity.WebActivity;
import luluteam.bath.bathprojectas.model.AllDeviceWorkInfo;
import luluteam.bath.bathprojectas.model.RunDeviceDetail;
import luluteam.bath.bathprojectas.utils.ToastUtil;
import luluteam.bath.bathprojectas.video.VideoMainActivity;

public class JumpHelper {

    /**
     * 跳转到登录页面
     *
     * @param context
     * @param singleLogin
     */
    public static void gotoLoginActivity(Context context, boolean singleLogin) {
        // FLAG_ACTIVITY_CLEAR_TASK:如果在调用Context.startActivity时传递这个标记，
        // 将会导致任何用来放置该activity的已经存在的task里面的已经存在的activity先清空，然后该activity再在该task中启动。
        // 也就是说，这个新启动的activity变为了这个空task的根activity，所有老的activity都结束掉。
        // 该标志必须和FLAG_ACTIVITY_NEW_TASK一起使用
//        Intent intent = new Intent(context, LoginActivity.class);
        Intent intent = new Intent(context, Login2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("singleLogin", singleLogin);
        context.startActivity(intent);
    }

    public static void gotoAboutActivity(Context context) {
        Intent aboutIntent = new Intent(context, AboutActivity.class);
        context.startActivity(aboutIntent);
    }

    /**
     * 视频播放列表Activity
     *
     * @param context
     * @param toiletId 想要查看监控的厕所id
     */
    public static void gotoVideoActivity(Context context, String toiletId) {
        Intent videoIntent = new Intent(context, VideoMainActivity.class);
        if (StringUtils.isNotEmpty(toiletId)) {
            videoIntent.putExtra(VideoMainActivity.EXTRA_NAME_TOILETID, toiletId);
        }
//        Intent videoIntent = new Intent(context, WebVideoActivity.class);
        context.startActivity(videoIntent);
    }

    public static void gotoWebActivity(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void gotoUpdateActivity(Context context) {
        Intent intent = new Intent(context, UpdateActivity.class);
        context.startActivity(intent);
    }

    public static void gotoPitActivity(Context context, String toiletId, int selectedTab) {
        Intent intent = new Intent(context, Pit2Activity.class);
        intent.putExtra(Pit2Activity.EXTRA_NAME_TOILETID, toiletId);
        intent.putExtra(Pit2Activity.EXTRA_NAME_SELECTED_TAB, selectedTab);
        context.startActivity(intent);
    }

    public static void gotoDisplayFragmentActivity(Context context, Class clazz, String toolbarTitle) {
        Intent intent = new Intent(context, DisplayFragmentActivity.class);
        intent.putExtra(DisplayFragmentActivity.EXTRA_NAME_TARGET_FRAGMENT, clazz.getName());
        intent.putExtra(DisplayFragmentActivity.EXTRA_NAME_TOOLBAR_TITLE, toolbarTitle);
        context.startActivity(intent);

    }

    public static void gotoDeviceRunDetailActivity(Context context, AllDeviceWorkInfo currentInfo) {
        Intent intent = new Intent(context, DeviceRunDetailActivity.class);

        if (currentInfo != null && currentInfo.getVersion() == AllDeviceWorkInfo.VERSION.VERSION_2) {
            List<RunDeviceDetail> manDevice = AllDeviceWorkInfo.TO_DETAIL(currentInfo.getManDeviceValue(), currentInfo.getCommonDeviceValue());
            List<RunDeviceDetail> womanDevice = AllDeviceWorkInfo.TO_DETAIL(currentInfo.getWomanDeviceValue(), currentInfo.getCommonDeviceValue());
            List<RunDeviceDetail> thirdDevice = AllDeviceWorkInfo.TO_DETAIL(currentInfo.getDisableDeviceValue(), currentInfo.getCommonDeviceValue());


            intent.putExtra(DeviceRunDetailActivity.MAN, (Serializable) manDevice);
            intent.putExtra(DeviceRunDetailActivity.WOMAN, (Serializable) womanDevice);
            intent.putExtra(DeviceRunDetailActivity.THIRD, (Serializable) thirdDevice);
            context.startActivity(intent);
        } else {
            ToastUtil.logAndToast(context, "当前厕所非二期板或未在线！");
        }


    }


}
