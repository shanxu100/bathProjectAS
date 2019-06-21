package luluteam.bath.bathprojectas.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jjy on 16-5-7.
 */
public class SQLLiteInstance {
    private static SQLiteDatabase instance = null;
    private static final String dbName = "bathProjectAS.db";

    private SQLLiteInstance() {
    }

    public static SQLiteDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLLiteInstance.class) {
                if (instance == null) {
                    instance = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
                }
            }
        }
        return instance;
    }
}
