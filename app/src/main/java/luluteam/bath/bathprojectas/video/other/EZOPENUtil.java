package luluteam.bath.bathprojectas.video.other;

/**
 * @author Guan
 * @date Created on 2018/5/28
 */
public class EZOPENUtil {

    private static final String TAG = "EZOPENUtil";


    /**
     * 获取EZOPEN协议的 直播地 址
     * 预览：ezopen://[验证码@]open.ys7.com/序列号/通道号[.清晰度].live[?参数]
     * 文档：https://open.ys7.com/doc/zh/readme/ezopen.html
     *
     * @param deviceSerial
     * @param channelNo
     * @return
     */
    public static String getLiveUrl(String deviceSerial, int channelNo, boolean isHD) {

        StringBuilder sb = new StringBuilder(VideoConstant.getBaseVideoURL());
        sb.append("/")
                .append(deviceSerial)
                .append("/")
                .append(channelNo)
                .append(isHD ? ".hd" : "")
                .append(".live");
        return sb.toString();
    }

    /**
     * 获取EZOPEN协议的 回放 地址
     * 回放：ezopen://[验证码@]open.ys7.com/序列号/通道号[.回放源].rec[?参数]
     * 文档：https://open.ys7.com/doc/zh/readme/ezopen.html
     *
     * @param deviceSerial
     * @param channelNo
     * @return
     */
    public static String getRecUrl(String deviceSerial, int channelNo) {

        StringBuilder sb = new StringBuilder(VideoConstant.getBaseVideoURL());
        sb.append("/")
                .append(deviceSerial)
                .append("/")
                .append(channelNo)
                .append(".rec");
        return sb.toString();
    }


}
