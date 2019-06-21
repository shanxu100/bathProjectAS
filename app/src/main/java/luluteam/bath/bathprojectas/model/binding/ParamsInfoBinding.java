package luluteam.bath.bathprojectas.model.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.text.ParseException;

import luluteam.bath.bathprojectas.BR;
import luluteam.bath.bathprojectas.model.ParamsInfo;
import luluteam.bath.bathprojectas.utils.TimeUtil;

/**
 * 用于数据绑定：参数设置页面
 *
 * @author Guan
 * @date Created on 2018/5/7
 */
public class ParamsInfoBinding extends BaseObservable {
    private String toiletId;
    private String time;
    private Threshold thresholdLight;
    private Group group;

    public ParamsInfoBinding() {
        group = new Group();
        thresholdLight = new Threshold();
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    @Bindable
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    public Threshold getThresholdLight() {
        return thresholdLight;
    }

    public void setThresholdLight(Threshold thresholdLight) {
        this.thresholdLight = thresholdLight;
    }

    @Bindable
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * 更新值
     *
     * @param params
     * @param detectorPosition 光照探头spinner的position
     * @param timerPosition
     */
    public void updateValue(ParamsInfo.Params params, int detectorPosition, int timerPosition) {
        this.toiletId = new String(params.getCardId());
        try {
//            this.time = TimeUtil.getTime(params.getSystemClock(), "yyMMddHHmmss", "yyyy-MM-dd'T'HH:mm:ss");
            setTime(TimeUtil.getTime(params.getSystemClock(), "yyMMddHHmmss", "yyyy-MM-dd'T'HH:mm:ss"));
            System.out.println("time:" + this.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (detectorPosition == 0) {
            thresholdLight.updateValue(params.getGroupMan().getThresholdLight() + "", "1");
        } else if (detectorPosition == 1) {
            thresholdLight.updateValue(params.getGroupWoman().getThresholdLight() + "", "2");
        } else if (detectorPosition == 2) {
            thresholdLight.updateValue(params.getGroupDisabled().getThresholdLight() + "", "3");
        }
        System.out.println("thresholdLight:" + thresholdLight.toString());

        if (timerPosition == 0) {
            this.group.updateValue(params.getGroupMan(), "1");
        } else if (timerPosition == 1) {
            this.group.updateValue(params.getGroupWoman(), "2");
        } else if (timerPosition == 2) {
            this.group.updateValue(params.getGroupDisabled(), "3");
        }
        System.out.println("group:" + group.toString());
    }

    /**
     * 未完成
     * 根据收到的ParamsInfo数据，生成显示在页面上的ParamsInfoBinding对象
     * 然后用这个对象和当前的ParamsInfoBinding对象比较
     * 如果相同，则说明“设置成功”
     *
     * @param paramsInfo
     * @return
     */
    public ParamsInfoBinding generateParamsInfoBinding(ParamsInfo paramsInfo) {
        ParamsInfoBinding mParams = new ParamsInfoBinding();

        ParamsInfo.Params params = paramsInfo.getValue();
        mParams.setToiletId(params.getCardId());
        mParams.setTime(params.getSystemClock());

        //光照探头值
        Threshold threshold = new Threshold();
        threshold.setUsage(getThresholdLight().getUsage());
        if (threshold.getUsage().equals("1")) {
            threshold.setThresholdValue(params.getGroupMan().getThresholdLight() + "");
        } else if (threshold.getUsage().equals("2")) {
            threshold.setThresholdValue(params.getGroupWoman().getThresholdLight() + "");
        } else if (threshold.getUsage().equals("3")) {
            threshold.setThresholdValue(params.getGroupDisabled().getThresholdLight() + "");
        }
        mParams.setThresholdLight(threshold);


        Group group = new Group();
        group.setUsage(getGroup().getUsage());
        if (group.getUsage().equals("1")) {
//            group.setTimerLight();
        } else if (group.getUsage().equals("2")) {
//            group.setThresholdValue(params.getGroupWoman().getThresholdLight() + "");
        } else if (group.getUsage().equals("3")) {
//            group.setThresholdValue(params.getGroupDisabled().getThresholdLight() + "");
        }


        return mParams;

    }

    /**
     * 比较
     *
     * @param paramsInfoBinding
     * @return
     */
    public boolean compare(ParamsInfoBinding paramsInfoBinding) {
        if (!toiletId.equals(paramsInfoBinding.getToiletId())) {
            return false;
        }

        if (thresholdLight.compare(paramsInfoBinding.getThresholdLight())
                || !group.compare(paramsInfoBinding.getGroup())) {
            return false;
        }

        return true;
    }

    public void clear() {
        this.toiletId = "";
        this.time = "";
        this.thresholdLight.clear();
        this.group.clear();
    }

    public static final class Threshold extends BaseObservable {
        private String thresholdValue = "";
        private String usage = "";

        @Bindable
        public String getThresholdValue() {
            return thresholdValue;
        }

        public void setThresholdValue(String thresholdValue) {
            this.thresholdValue = thresholdValue;
            notifyPropertyChanged(BR.thresholdValue);
        }

        @Bindable
        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
            notifyPropertyChanged(BR.usage);
        }

        /**
         * 更新值
         *
         * @param thresholdValue
         * @param usage
         */
        public void updateValue(String thresholdValue, String usage) {
            setThresholdValue(thresholdValue);
            setUsage(usage);
        }

        /**
         * 比较
         *
         * @param threshold
         * @return
         */
        public boolean compare(Threshold threshold) {
            if (!thresholdValue.equals(threshold.thresholdValue)
                    || !thresholdValue.equals(threshold.usage)) {
                return false;
            }
            return true;
        }

        public void clear() {
            this.thresholdValue = "";
            this.usage = "";
        }
    }

    public static final class Group extends BaseObservable {
        private String usage = "";
        private TimerParams timerLight;
        private TimerParams timerAudio;
        private TimerParams timerSterilamp;

        public Group() {
            this.timerLight = new TimerParams();
            this.timerAudio = new TimerParams();
            this.timerSterilamp = new TimerParams();
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        @Bindable
        public TimerParams getTimerLight() {
            return timerLight;
        }

        public void setTimerLight(TimerParams timerLight) {
            this.timerLight = timerLight;
        }

        @Bindable
        public TimerParams getTimerAudio() {
            return timerAudio;
        }

        public void setTimerAudio(TimerParams timerAudio) {
            this.timerAudio = timerAudio;
        }

        @Bindable
        public TimerParams getTimerSterilamp() {
            return timerSterilamp;
        }

        public void setTimerSterilamp(TimerParams timerSterilamp) {
            this.timerSterilamp = timerSterilamp;
        }

        /**
         * 更新值
         *
         * @param group
         * @param usage man-woman-disabled:1-2-3
         */
        public void updateValue(ParamsInfo.Group group, String usage) {
            this.usage = usage;
            this.timerAudio.updateValue(group.getTimerAudio());
            this.timerLight.updateValue(group.getTimerLight());
            this.timerSterilamp.updateValue(group.getTimerSterilamp());
        }

        /**
         * 比较
         *
         * @param group
         * @return
         */
        public boolean compare(Group group) {
            if (!group.getUsage().equals(this.usage)) {
                return false;
            }
            if (!group.getTimerAudio().compare(this.timerAudio)) {
                return false;
            }
            if (!group.getTimerLight().compare(this.timerLight)) {
                return false;
            }
            if (!group.getTimerSterilamp().compare(this.timerSterilamp)) {
                return false;
            }

            return true;
        }

        public void clear() {
            this.usage = "";
            this.timerAudio.clear();
            this.timerLight.clear();
            this.timerSterilamp.clear();
        }
    }

    public static final class TimerParams extends BaseObservable {
        private String startTime = "";
        private String stopTime = "";
        private String duration = "";


        @Bindable
        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
            notifyPropertyChanged(BR.startTime);
        }

        @Bindable
        public String getStopTime() {
            return stopTime;
        }

        public void setStopTime(String stopTime) {
            this.stopTime = stopTime;
            notifyPropertyChanged(BR.stopTime);
        }

        @Bindable
        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
            notifyPropertyChanged(BR.duration);
        }


        /**
         * 更新值
         *
         * @param timerParams
         */
        public void updateValue(ParamsInfo.TimerParams timerParams) {
            setStartTime(TimeUtil.secondToHHmmss(timerParams.getStartTime()));
            setStopTime(TimeUtil.secondToHHmmss(timerParams.getStopTime()));
            setDuration((timerParams.getDuration_s() / 60) + "");
        }

        /**
         * 比较
         *
         * @param timerParams
         * @return
         */
        public boolean compare(TimerParams timerParams) {
            if (timerParams.getDuration().equals(this.duration)) {
                return false;
            }
            if (!timerParams.getStartTime().equals(this.startTime)) {
                return false;
            }
            if (!timerParams.getStopTime().equals(this.stopTime)) {
                return false;
            }

            return true;
        }

        public void clear() {
            this.startTime = "";
            this.stopTime = "";
            this.duration = "";
        }

    }


    private interface IParamsObject {
        void clear();
    }
}
