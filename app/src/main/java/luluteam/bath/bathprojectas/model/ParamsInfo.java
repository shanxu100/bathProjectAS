package luluteam.bath.bathprojectas.model;

import com.google.gson.Gson;

/**
 * 对应服务器传送过来的参数设置
 *
 * @author Guan
 */

public class ParamsInfo {
    private String toiletId;
    /**
     * yyMMddHHmmss
     */
    private String time;
    private int length;
    private Params value;

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Params getValue() {
        return value;
    }

    public void setValue(Params value) {
        this.value = value;
    }

    public static final class Params {
        private String cardId;
        private String systemClock;
        private Group GroupMan;
        private Group GroupWoman;
        private Group GroupDisabled;

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getSystemClock() {
            return systemClock;
        }

        public void setSystemClock(String systemClock) {
            this.systemClock = systemClock;
        }

        public Group getGroupMan() {
            return GroupMan;
        }

        public void setGroupMan(Group groupMan) {
            GroupMan = groupMan;
        }

        public Group getGroupWoman() {
            return GroupWoman;
        }

        public void setGroupWoman(Group groupWoman) {
            GroupWoman = groupWoman;
        }

        public Group getGroupDisabled() {
            return GroupDisabled;
        }

        public void setGroupDisabled(Group groupDisabled) {
            GroupDisabled = groupDisabled;
        }


        /**
         * 比较
         *
         * @param params
         * @return
         */
        public boolean compare(Params params) {
            if (!params.getCardId().equals(this.cardId)) {
                return false;
            }
            //时间不比较
            if (params.getSystemClock().equals(this.systemClock)) {

            }
            if (!(params.getGroupMan().compare(this.GroupMan))) {
                return false;
            }
            if (!(params.getGroupWoman().compare(this.GroupWoman))) {
                return false;
            }
            if (!(params.getGroupDisabled().compare(this.GroupDisabled))) {
                return false;
            }
            return true;
        }
    }

    public static final class Group {
        private String usage;
        private int thresholdLight;
        private int thresholdBody;
        private TimerParams TimerLight;
        private TimerParams TimerAudio;
        private TimerParams TimerSterilamp;

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }

        public int getThresholdLight() {
            return thresholdLight;
        }

        public void setThresholdLight(int thresholdLight) {
            this.thresholdLight = thresholdLight;
        }

        public int getThresholdBody() {
            return thresholdBody;
        }

        public void setThresholdBody(int thresholdBody) {
            this.thresholdBody = thresholdBody;
        }

        public TimerParams getTimerLight() {
            return TimerLight;
        }

        public void setTimerLight(TimerParams timerLight) {
            TimerLight = timerLight;
        }

        public TimerParams getTimerAudio() {
            return TimerAudio;
        }

        public void setTimerAudio(TimerParams timerAudio) {
            TimerAudio = timerAudio;
        }

        public TimerParams getTimerSterilamp() {
            return TimerSterilamp;
        }

        public void setTimerSterilamp(TimerParams timerSterilamp) {
            TimerSterilamp = timerSterilamp;
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
            if (group.getThresholdBody() != this.thresholdBody) {
                return false;
            }
            if (group.getThresholdLight() != this.thresholdLight) {
                return false;
            }
            if (!group.getTimerAudio().compare(this.TimerAudio)) {
                return false;
            }
            if (!group.getTimerLight().compare(this.TimerLight)) {
                return false;
            }
            if (!group.getTimerSterilamp().compare(this.TimerSterilamp)) {
                return false;
            }

            return true;
        }
    }

    public static final class TimerParams {
        private int duration_s;
        private String startTime;
        private String stopTime;

        public int getDuration_s() {
            return duration_s;
        }

        public void setDuration_s(int duration_s) {
            this.duration_s = duration_s;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStopTime() {
            return stopTime;
        }

        public void setStopTime(String stopTime) {
            this.stopTime = stopTime;
        }

        /**
         * 比较
         *
         * @param timerParams
         * @return
         */
        public boolean compare(TimerParams timerParams) {
            if (timerParams.getDuration_s() != this.duration_s) {
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
    }

    /**
     * 将model转化为json，方便存入数据库
     *
     * @return
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * 比较
     *
     * @param paramsInfo
     * @return
     */
    public boolean compare(ParamsInfo paramsInfo) {
        if (!paramsInfo.getToiletId().equals(this.toiletId)) {
            return false;
        }
        if (paramsInfo.getLength() != this.length) {
            return false;
        }
        //时间不比较
        if (!paramsInfo.getTime().equals(this.time)) {
            //return false;
        }
        if (!paramsInfo.getValue().compare(this.value)) {
            return false;
        }
        return true;
    }
}
