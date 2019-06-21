package luluteam.bath.bathprojectas.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import luluteam.bath.bathprojectas.app.App;

/**
 * Created by JJY on 2016/8/24.
 */
public class ToastUtil {

    private static Handler handler;

    public static void showShortToast(Context context, String content) {
        showShortToast(context, content, Gravity.TOP);
    }

    public static void showLongToast(Context context, String content) {
        showLongToast(context, content, Gravity.TOP);
    }

    /**
     * 支持在 <P>非UI线程<P/> 中显示Toast
     * 非线程安全
     *
     * @param context
     * @param msg
     */
    public static void logAndToast(final Context context, final String msg) {
        logAndToast(context, msg, Gravity.TOP);
    }

    public static void showShortToast(Context context, String content, int gravity) {
        if (context == null) {
            return;
        }
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast toast = Toast.makeText(App.getAppContext(), content, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    public static void showLongToast(Context context, String content, int gravity) {
        if (context == null) {
            return;
        }
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast toast = Toast.makeText(App.getAppContext(), content, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 0);
            toast.show();
        }
    }

    /**
     * 支持在 <P>非UI线程<P/> 中显示Toast
     * 非线程安全
     *
     * @param context
     * @param msg
     */
    public static void logAndToast(final Context context, final String msg, int gravity) {
        if (context == null) {
            return;
        }
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(App.getAppContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(gravity, 0, 0);
                toast.show();
            }
        });
    }

}
