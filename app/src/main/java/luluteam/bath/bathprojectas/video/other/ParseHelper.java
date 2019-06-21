package luluteam.bath.bathprojectas.video.other;

/**
 * @author Guan
 * @date Created on 2018/6/17
 */
public class ParseHelper {

    /**
     * @param status
     * @return
     */
    public static String parseStatus(int status) {
        switch (status) {
            case 0:
                return "不在线";
            case 1:
                return "在线";
            default:
                return "未知";
        }
    }

    /**
     * @param videoLevel
     * @return
     */
    public static String parseVideoLevel(int videoLevel) {
        String vl = "";
        switch (videoLevel) {
            case 0:
                vl = "流畅";
                break;
            case 1:
                vl = "均衡";
                break;
            case 2:
                vl = "高清";
                break;
            case 3:
                vl = "超清";
                break;
            default:
                vl = "未知";
        }
        return vl;
    }


    /**
     * @param isEncrypt
     * @return
     */
    public static String parseIsEncrypt(int isEncrypt) {
        String encrypt = "";
        if (isEncrypt == 0) {
            encrypt = "未加密";
        } else {
            encrypt = "加密";
        }
        return encrypt;
    }

}
