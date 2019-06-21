package luluteam.bath.bathprojectas.model.RemoteControl;

/**
 * 单个设备的运行记录
 * Created by 123 on 2017/10/25.
 */

public class DeviceMessage {
    private String time;
    private int flag;
    private int times;
    private String reason;
    private String deviceType;
    private boolean action;
    //usage 应该为String
    private int usage;

    @Override
    public String toString() {
        return "DeviceMessage{" +
                "time='" + time + '\'' +
                ", flag=" + flag +
                ", times=" + times +
                ", reason='" + reason + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", action=" + action +
                ", usage=" + usage +
                ", toiletId='" + toiletId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }

    private String toiletId;
    private String deviceId;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
