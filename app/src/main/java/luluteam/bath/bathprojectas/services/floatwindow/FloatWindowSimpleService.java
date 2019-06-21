package luluteam.bath.bathprojectas.services.floatwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import luluteam.bath.bathprojectas.R;
import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.tools.EventBusManager;
import luluteam.bath.bathprojectas.utils.ToastUtil;


/**
 * 这是一个简易的悬浮窗Service，只包含一个小窗口
 */
public class FloatWindowSimpleService extends Service {

    private static final String TAG = "FloatWindowSimpleService";

    private WindowManager windowManager;

    /**
     * 布局
     */
    private RelativeLayout simple_LL_view;//View的子类

    private LinearLayout websocketstate_ll, mqttstate_ll;
    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public FloatWindowSimpleService() {
    }

    @Override
    public void onCreate() {
        windowManager = (WindowManager) App.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        super.onCreate();
        if (!EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).isRegistered(this)) {
            EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).register(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createFloatView();
        executorService.scheduleAtFixedRate(new RefreshTask(), 3, 2, TimeUnit.SECONDS);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        executorService.shutdownNow();
        removeFloatView();
        EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).unregister(this);
        super.onDestroy();
    }
    //========================================================================================

    /**
     * 创建悬浮窗
     */
    private void createFloatView() {

        if (isFloatWindowShowing()) {
            return;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        simple_LL_view = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.network_state, null);
        websocketstate_ll = (LinearLayout) simple_LL_view.findViewById(R.id.websocketstate_ll);
        mqttstate_ll = (LinearLayout) simple_LL_view.findViewById(R.id.mqttstate_ll);
        try {
            windowManager.addView(simple_LL_view, params);
        } catch (Exception e) {
            ToastUtil.showLongToast(getApplicationContext(), "悬浮窗显示异常，请手动授予权限");
            e.printStackTrace();
        }

        simple_LL_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }


    private void removeFloatView() {
        try {
            //TODO 这里会出现View not attached to window manager的异常，可能因为销毁view的时候和activity之间的交互没做好
            //先try-catch了，后面再优化
            if (simple_LL_view != null) {
                windowManager.removeView(simple_LL_view);
                simple_LL_view = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 判断当前悬浮窗是否显示
     *
     * @return
     */
    private boolean isFloatWindowShowing() {
        return simple_LL_view != null;
    }

    /**
     * 设置悬浮窗的显示方式：本app在前台时，不显示悬浮窗；本app后台工作时，显示悬浮窗
     */
    private class RefreshTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (App.getInstance().isAppForeground() && simple_LL_view != null && simple_LL_view.getVisibility() == View.GONE) {
                        // 当前界面不是本应用程序，且没有悬浮窗显示，则创建悬浮窗。
                        simple_LL_view.setVisibility(View.VISIBLE);
                    } else if (!App.getInstance().isAppForeground() && simple_LL_view != null && simple_LL_view.getVisibility() == View.VISIBLE) {
                        // 当前界面是本应用程序，且有悬浮窗显示，则移除悬浮窗。
                        simple_LL_view.setVisibility(View.GONE);
                    }
                }
            });

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMessage(EventBusManager.EventBusMsg msg) {
        Log.e(TAG, msg.toString());
        if (msg.msgType == EventBusManager.MsgType.fromWebSocket) {
            if (msg.isAction()) {
                websocketstate_ll.setVisibility(View.GONE);
            } else {
                websocketstate_ll.setVisibility(View.VISIBLE);
            }
        } else if (msg.msgType == EventBusManager.MsgType.fromMqtt) {
            if (msg.isAction()) {
                mqttstate_ll.setVisibility(View.GONE);
            } else {
                mqttstate_ll.setVisibility(View.VISIBLE);
            }
        }

    }

}
