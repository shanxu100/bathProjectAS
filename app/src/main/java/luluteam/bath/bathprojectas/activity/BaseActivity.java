package luluteam.bath.bathprojectas.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import luluteam.bath.bathprojectas.app.AppManager;

/**
 * Created by Guan on 2017/12/11.
 */

public class BaseActivity extends AppCompatActivity {

    public Context context;
    protected String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        //将Activity实例添加到AppManager的堆栈
        AppManager.getAppManager().addActivity(this);
        setStatusBarColor(this, Color.BLACK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity实例从AppManager的堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    /**
     * 获取当前显示的Activity
     *
     * @return
     */
    protected Activity getCurrentActivity() {
        return AppManager.getAppManager().currentActivity();
    }

    /**
     * 设置状态栏
     *
     * @param activity
     * @param statusColor
     */
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }


    public interface FragmentCallback {
        /**
         * 由男厕、女厕、残卫的子Fragment进行override，父fragment调用更新
         *
         * @param object
         */
        void onStateChanged(Object object);

        /**
         * 在ControlFragment 中 override该方法，在activity中调用更新Image
         */
        void onReloadImage();

        /**
         * 在ControlFragment 中 override该方法，在activity中调用更新整体状态
         */
        void onNewToilet();

    }
}
