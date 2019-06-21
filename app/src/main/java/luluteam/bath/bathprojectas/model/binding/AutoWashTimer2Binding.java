package luluteam.bath.bathprojectas.model.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import luluteam.bath.bathprojectas.BR;
import luluteam.bath.bathprojectas.utils.TimeUtil;

public class AutoWashTimer2Binding extends BaseObservable {
    private String toiletId;
    private char usage;
    private char deviceType;
    //自动冲水定时器开始时间
    private String startTime;
    //自动冲水定时器结束时间
    private String endTime;
    //冲水前的延时时间，这个值需要小于endTime-startTime,单位：分钟
    private String washDelay;
    //播放语音提示长度
    private String voiceLength;
    //增压泵开启延时时间
    private String boosterPumpDelayTime;

    public AutoWashTimer2Binding(char usage, char deviceType, String startTime, String endTime, String washDelay, String voiceLength, String boosterPumpDelayTime) {

        this.usage = usage;
        this.deviceType = deviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.washDelay = washDelay;
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
    public String getWashDelay() {
        return washDelay;
    }

    public void setWashDelay(String washDelay) {
        this.washDelay = washDelay;
        notifyPropertyChanged(BR.washDelay);
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

    @Override
    public String toString() {
        return "{startTime=" + startTime + "endTime=" + endTime + "washDelay=" + washDelay + "voiceLength=" + voiceLength + "boosterPumpDelayTime=" + boosterPumpDelayTime;
    }
}
