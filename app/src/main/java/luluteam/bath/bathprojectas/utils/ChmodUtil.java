package luluteam.bath.bathprojectas.utils;

import java.io.IOException;

/**
 * Created by Guan on 2018/1/15.
 */

public class ChmodUtil {

    public static void chmod(String permission, String fullPath) {
        try {
            String cmd = "chmod " + permission + " " + fullPath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
