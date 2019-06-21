package luluteam.bath.bathprojectas.constants;

import java.util.HashMap;

import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.result.UserResult;
import luluteam.bath.bathprojectas.model.result.ValueResult;

/**
 * Created by 123 on 2017/10/23.
 */

public class APPConstant {
    //apk自动更新功能post参数
    public static final String _API_KEY = "c6916e539d4320f06887822ed71b8cbb";
    public static final String APP_KEY = "308d5034522411f5f2e75fa753c06c62";

    public static String ANDROID_ID = "";
    public static String TOILETID = "";
    public static String USAG = "";
    public static int TOILET_VERSION = 1;
    public static String TOILET_NICKNAME = "";

    /**
     * 通过USERINFO字段获取USERNAME
     */
    @Deprecated
    public static String USERNAME = "";
    public static UserResult.UserInfo USERINFO = null;

    //所选区域
    public static String PROVIENCE = "";
    public static String CITY = "";
    public static String COUNTY = "";
    public static String DETAIL = "";
//    public static final String BASE_DIR= Environment.getExternalStoragePublicDirectory();

    public static ValueResult BODY_YESTERDAY_MAX_VALUE = null;

    /**
     * 现在写死，以后是需要到后台获取的
     */
    public static String[] TOILETIDS = {"10010000", "00000001", "00000002", "00000003", "00000004",
            "00000005", "00000006", "00000007", "00000008", "00000009", "00000010", "12345678"};

    /**
     * 屏幕宽度
     */
    public static int SCREEN_WIDTH;
    /**
     * 屏幕高度
     */
    public static int SCREEN_HEIGHT;

    public static class SharedPreferenceStr {
        //登录时，选择服务器
        public static final String CHECKED_SERVER = "checkedServer";
        public static final String SAVED_USERNAME = "savedusername";
        public static final String SAVED_NICKNAME = "savednickname";
        public static final String SAVED_PWD = "savedpwd";

    }

    /**
     * 每次增加设备需要在这里添加设备名方法后缀，前缀也需要按要求格式进行编写，方便反射执行，这样不需要每增加一个设备更改很多地方
     * 好像每次增加一个设备，还是要更改三个文件，AppConstant,Devices,AllDevicesMessage
     */
    public static String[] deviceNames = new String[]{
            "MainLight", "SecondaryLight", "AirPump", "Audio",
            "Sterilamp", "WindMachine", "door", "lightDetector", "FloorWash"
    };

    public static final HashMap<String, String> UsageMap = new HashMap<>();
    public static final HashMap<String, String> deviceTypeMap = new HashMap<>();

    static {
        UsageMap.put("男厕", "1");
        UsageMap.put("女厕", "2");
        UsageMap.put("残卫", "3");
        UsageMap.put("公用", "4");

        UsageMap.put("1", "男厕");
        UsageMap.put("2", "女厕");
        UsageMap.put("3", "残卫");
        UsageMap.put("4", "公用");

        //========================
        deviceTypeMap.put("主灯", "A");
        deviceTypeMap.put("辅灯", "B");
        deviceTypeMap.put("抽空风机", "C");
        deviceTypeMap.put("音响", "D");
        deviceTypeMap.put("消毒灯", "E");
        deviceTypeMap.put("一键冲洗", "H");
        deviceTypeMap.put("风幕机", "I");
        deviceTypeMap.put("水表", "Q");
        deviceTypeMap.put("电表", "R");
        deviceTypeMap.put("冲水", "K");
        deviceTypeMap.put("门设备", "T");


        deviceTypeMap.put("A", "主灯");
        deviceTypeMap.put("B", "辅灯");
        deviceTypeMap.put("C", "抽空风机");
        deviceTypeMap.put("D", "音响");
        deviceTypeMap.put("E", "消毒灯");
        deviceTypeMap.put("H", "一键冲洗");
        deviceTypeMap.put("I", "风幕机");
        deviceTypeMap.put("Q", "水表");
        deviceTypeMap.put("R", "电表");
        deviceTypeMap.put("K", "冲水");
        deviceTypeMap.put("T", "门设备");
    }

    public volatile static Bus485Pit.ItemCommon itemCommon = Bus485Pit.ItemCommon.buildEmptyInstance();


}
