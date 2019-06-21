package luluteam.bath.bathprojectas.model.StatisticAnalysis;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luluteam on 2017/10/26.
 */

public class RecordInfoItem {
    @SerializedName("x")
    private String date;        //日期
    @SerializedName("value")
    private int value;       //累计使用的时间
    @SerializedName("times")
    private int times;          //累计使用的次数

    public RecordInfoItem(String date, int value, int times) {
        this.date = date;
        this.value = value;
        this.times = times;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
