package luluteam.bath.bathprojectas.model.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import luluteam.bath.bathprojectas.BR;
import luluteam.bath.bathprojectas.model.AutoWashTimerInfo;
import luluteam.bath.bathprojectas.utils.TimeUtil;

public class AutoWashTimerBinding extends BaseObservable {
    private String toiletId;
    private char usage;
    private char deviceType;
    //自动冲水定时器开始时间
    private String startTime;
    //自动冲水定时器结束时间
    private String endTime;
    //语音提示间隔
    private String voiceInterval;
    //播放语音提示长度
    private String voiceLength;
    //增压泵开启延时时间
    private String boosterPumpDelayTime;

    private AutoWashTimerInfo autoWashTimerInfo;

    public AutoWashTimerInfo getAutoWashTimerInfo() {
        return autoWashTimerInfo;
    }

    public void setAutoWashTimerInfo(AutoWashTimerInfo autoWashTimerInfo) {
        this.autoWashTimerInfo = autoWashTimerInfo;
    }

    public AutoWashTimerBinding(char usage, char deviceType, String startTime, String endTime, String voiceInterval, String voiceLength, String boosterPumpDelayTime) {
        this.usage = usage;
        this.deviceType = deviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.voiceInterval = voiceInterval;
        this.voiceLength = voiceLength;
        this.boosterPumpDelayTime = boosterPumpDelayTime;
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

    @Bindable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        notifyPropertyChanged(BR.endTime);
    }

    @Bindable
    public String getVoiceInterval() {
        return voiceInterval;
    }

    public void setVoiceInterval(String voiceInterval) {
        this.voiceInterval = voiceInterval;
        notifyPropertyChanged(BR.voiceInterval);
    }

    @Bindable
    public String getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(String voiceLength) {
        this.voiceLength = voiceLength;
        notifyPropertyChanged(BR.voiceLength);
    }

    @Bindable
    public String getBoosterPumpDelayTime() {
        return boosterPumpDelayTime;
    }

    public void setBoosterPumpDelayTime(String boosterPumpDelayTime) {
        this.boosterPumpDelayTime = boosterPumpDelayTime;
        notifyPropertyChanged(BR.boosterPumpDelayTime);
    }

    public void updateValue() {
        notifyChange();
        try {
            setStartTime(TimeUtil.getTime(startTime, "HHmmss", "HH:mm:ss"));
            setEndTime(TimeUtil.getTime(endTime, "HHmmss", "HH:mm:ss"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deepCopy(AutoWashTimerInfo.Item item) {
        this.setStartTime(item.startTime);
        this.setEndTime(item.endTime);
        this.setVoiceInterval(item.voiceInterval + "");
        this.setVoiceLength(item.voiceLength + "");
        this.setBoosterPumpDelayTime(item.boosterPumpDelayTime + "");
    }

    @Override
    public String toString() {
        return "{startTime=" + startTime + "endTime=" + endTime + "voiceInterval=" + voiceInterval + "voiceLength=" + voiceLength + "boosterPumpDelayTime=" + boosterPumpDelayTime;
    }
}
