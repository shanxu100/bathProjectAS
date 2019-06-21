package luluteam.bath.bathprojectas.model.StatisticAnalysis;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by luluteam on 2017/10/26.
 */

public class RecordInfoResult {
    @SerializedName("result")
    private boolean result;
    @SerializedName("toiletId")
    private String toiletId;        //卫浴的id
    @SerializedName("usage")
    private String usage;           //卫浴的类型（男，女，公共，。。）
    @SerializedName("deviceType")
    private String deviceType;      //卫浴中设备的类型
    @SerializedName("dataList")
    private List<RecordInfoItem> dataList;      //卫浴中设备的数据列表

    public RecordInfoResult(String toiletId, String usage, String deviceType, List<RecordInfoItem> dataList) {
        this.toiletId = toiletId;
        this.usage = usage;
        this.deviceType = deviceType;
        this.dataList = dataList;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<RecordInfoItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<RecordInfoItem> dataList) {
        this.dataList = dataList;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "RecordInfoResult{" +
                "result=" + result +
                ", toiletId='" + toiletId + '\'' +
                ", usage='" + usage + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
