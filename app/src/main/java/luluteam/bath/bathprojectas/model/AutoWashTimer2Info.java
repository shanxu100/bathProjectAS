package luluteam.bath.bathprojectas.model;


import luluteam.bath.bathprojectas.utils.GsonUtil;

public class AutoWashTimer2Info {
    private String toiletId;
    private char usage;
    private char deviceType;
    //自动冲水定时器开始时间
    public String startTime;
    //自动冲水定时器结束时间
    public String endTime;
    public int washDelay;
    //播放语音提示长度
    public int voiceLength;
    //增压泵开启延时时间
    public int boosterPumpDelayTime;

    public AutoWashTimer2Info() {
    }

    public AutoWashTimer2Info(String toiletId, char usage, char deviceType) {
        this.toiletId = toiletId;
        this.usage = usage;
        this.deviceType = deviceType;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public char getUsage() {
        return usage;
    }

    public void setUsage(char usage) {
        this.usage = usage;
    }

    public char getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(char deviceType) {
        this.deviceType = deviceType;
    }

    public String toJson() {
        return GsonUtil.toJson(this);
    }

    @Override
    public String toString() {
        return "AutoWashTimer2Info{" +
                "toiletId='" + toiletId + '\'' +
                ", usage=" + usage +
                ", deviceType=" + deviceType +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", washDelay=" + washDelay +
                ", voiceLength=" + voiceLength +
                ", boosterPumpDelayTime=" + boosterPumpDelayTime +
                '}';
    }
}
