package luluteam.bath.bathprojectas.utils;

/**
 * Created by TC on 2017/11/20.
 */

public class deviceTransformUtil {
    public static String deviceNameToDeviceType(String name) {
        switch (name) {
            case "主灯":
                return "A";
            case "辅灯":
                return "B";
            case "抽空风机":
                return "C";
            case "音响":
                return "D";
            case "消毒灯":
                return "E";
            case "地面冲洗":
                return "F";
            case "语音提示":
                return "G";
            case "人工按键":
                return "H";
            case "风幕机":
                return "I";

            case "光照探头\n(LUX)":
                return "J";
            case "人体感应\n探头(次)":
                return "K";
            case "灯光定时器":
                return "L";
            case "音响定时器":
                return "M";
            case "消毒灯定时器":
                return "N";
            case "地面冲洗定时器":
                return "O";
            case "系统时钟":
                return "P";
            case "水表读数(T)":
                return "Q";
            case "电表读数\n(kwh)":
                return "R";
            case "开关远程控制":
                return "S";
        }
        return "";
    }

    public static String deviceTypeTodeviceName(char c) {
        switch (c) {
            case 'A':
                return "主灯";
            case 'B':
                return "辅灯";
            case 'C':
                return "抽空风机";
            case 'D':
                return "音响";
            case 'E':
                return "消毒灯";
            case 'F':
                return "地面冲洗";
            case 'G':
                return "语音提示";
            case 'H':
                return "人工按键";
            case 'I':
                return "风幕机";

            case 'J':
                return "光照探头\n(LUX)";
            case 'K':
                return "人体感应\n探头(次)";
            case 'L':
                return "灯光定时器";
            case 'M':
                return "音响定时器";
            case 'N':
                return "消毒灯定时器";
            case 'O':
                return "地面冲洗定时器";
            case 'P':
                return "系统时钟";
            case 'Q':
                return "水表读数\n(T)";
            case 'R':
                return "电表读数\n(kwh)";
            case 'S':
                return "开关远程控制";
        }
        return "";
    }

    /**
     * 通过整形的usage返回对应的厕所中文
     *
     * @param usage
     * @return
     */
    public static String usageToBathName(int usage) {
        switch (usage) {
            case 1:
                return "男厕";
            case 2:
                return "女厕";
            case 3:
                return "残卫";
            case 4:
                return "公用";
            default:
                return "";
        }
    }

    public static String bathNameToUsage(String bathName) {
        switch (bathName) {
            case "男厕":
                return "1";
            case "女厕":
                return "2";
            case "残卫":
                return "3";
            case "公用":
                return "4";
            default:
                return "";
        }
    }
}
