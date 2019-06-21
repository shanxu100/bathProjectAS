package luluteam.bath.bathprojectas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Guan on 2017/12/21.
 */

public class TimeUtil {

    private TimeUtil() {
        throw new AssertionError("工具类不可实例化");
    }

    public static String secondToHHmmss(String second) {
        Date date = new Date(Integer.parseInt(second) * 1000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return format.format(date);

    }

    /**
     * 根据指定格式转换 日期显示格式
     *
     * @param inputTime
     * @param pattern_old
     * @param pattern_new
     * @return
     * @throws ParseException
     */
    public static String getTime(String inputTime, String pattern_old, String pattern_new) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern_old);
        Date date = format.parse(inputTime);
        format.applyPattern(pattern_new);
        return format.format(date);
    }

    /**
     * 得到现在时间与输入时间之间的差值（秒）
     *
     * @param inputTime
     * @return
     */
    public static long getDiffTime(String inputTime, String pattern) throws ParseException {
        Date inputDate = new SimpleDateFormat(pattern).parse(inputTime);
        return (System.currentTimeMillis() - inputDate.getTime()) / 1000;
    }

    /**
     * 得到两个字符串时间之间的差值（秒）
     *
     * @param startTime
     * @param endTime
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static long getDiffTime(String startTime, String endTime, String pattern) throws ParseException {
        Date startDate = new SimpleDateFormat(pattern).parse(startTime);
        Date endDate = new SimpleDateFormat(pattern).parse(endTime);
//        System.out.println("starttime:"+startDate);
        return (endDate.getTime() - startDate.getTime()) / 1000;

    }

    public static String getCurrentTime(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    public static void main(String[] args) {
//        System.out.print(secondToHHmmss("3600"));
        System.out.println(getCurrentTime("yyyy/MM/dd HH:mm:ss"));
    }
}
