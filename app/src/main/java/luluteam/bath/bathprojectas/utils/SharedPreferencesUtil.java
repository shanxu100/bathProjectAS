package luluteam.bath.bathprojectas.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JJY on 2016/9/14.
 */
public class SharedPreferencesUtil {
    public static SharedPreferences mySharedPreferences;

    public static void putString(Context context, String key, String value) {
        mySharedPreferences = context.getSharedPreferences("test", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        mySharedPreferences = context.getSharedPreferences("test", Activity.MODE_PRIVATE);
        mySharedPreferences = context.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String content = mySharedPreferences.getString(key, "");
        return content;
    }
}
