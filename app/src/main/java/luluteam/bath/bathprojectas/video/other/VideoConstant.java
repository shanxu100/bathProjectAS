package luluteam.bath.bathprojectas.video.other;


/**
 * @author Guan
 * @date Created on 2018/6/3
 */
public class VideoConstant {

    private enum BaseUrlType {
        //域名
        TYPE_DOMAIN,
        //IP
        TYPE_IP
    }

    public static BaseUrlType currentUrlType = BaseUrlType.TYPE_DOMAIN;

    public static final class Config {
        public static final String APPKEY = "48e12e587daa47fcb156a4fe053712c7";
        public static final String SECRET = "b6133d426531696c95fb7ed2f376676f";

//        public static final String APPKEY = "41b01cf05f30438084f4a4dd23b4f2af";
//        public static final String SECRET = "16b7d86c799e9c0f669d960b3e848851";
    }

    private static final String BASE_URL = "https://open.ys7.com";
    private static final String BASE_VIDEO_URL = "ezopen://open.ys7.com";

    private static final String BASE_URL_IP = "https://183.136.184.118";
    private static final String BASE_VIDEO_URL_IP = "ezopen://183.136.184.118";

    private static final String GET_ACCESS_TOKEN = "/api/lapp/token/get";
    private static final String GET_CAMERA_LIST = "/api/lapp/camera/list";

    public static String getAccessToken() {
        return getBaseURL() + GET_ACCESS_TOKEN;
    }

    public static String getCameraList() {
        return getBaseURL() + GET_CAMERA_LIST;
    }


    //=========================================================
    //
    //====================Helper===============================

    /**
     * 根据设置选择baseUrl
     *
     * @return
     */
    private static String getBaseURL() {
        if (currentUrlType == BaseUrlType.TYPE_DOMAIN) {
            return BASE_URL;
        } else if (currentUrlType == BaseUrlType.TYPE_IP) {
            return BASE_URL_IP;
        }
        //默认
        return BASE_URL;

    }


    /**
     * 根据设置选择baseVideoUrl
     * 使用IP，不能用。所以只能只用域名
     *
     * @return
     */
    public static String getBaseVideoURL() {
//        if (currentUrlType == BaseUrlType.TYPE_DOMAIN) {
//            return BASE_VIDEO_URL;
//        } else if (currentUrlType == BaseUrlType.TYPE_IP) {
//            return BASE_VIDEO_URL_IP;
//        }
        //默认
        return BASE_VIDEO_URL;
    }

}
