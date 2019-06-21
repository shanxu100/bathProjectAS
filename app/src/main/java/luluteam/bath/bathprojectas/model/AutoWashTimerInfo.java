package luluteam.bath.bathprojectas.model;

import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.utils.GsonUtil;

@Deprecated
public class AutoWashTimerInfo {
    private String toiletId = APPConstant.TOILETID;
    private char usage = '1';
    private char deviceType = 'X';
    private Item manItem;
    private Item womanItem;
    private Item disableItem;

    public AutoWashTimerInfo() {
        manItem = new Item("000000", "004000", "60", "15", "0");
        womanItem = new Item("000000", "004000", "60", "15", "0");
        disableItem = new Item("000000", "004000", "60", "15", "0");
    }

    public class Item {
        //自动冲水定时器开始时间
        public String startTime;
        //自动冲水定时器结束时间
        public String endTime;
        //语音提示间隔
        public String voiceInterval;
        //播放语音提示长度
        public String voiceLength;
        //增压泵开启延时时间
        public String boosterPumpDelayTime;

        public Item(String startTime, String endTime, String voiceInterval, String voiceLength, String boosterPumpDelayTime) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.voiceInterval = voiceInterval;
            this.voiceLength = voiceLength;
            this.boosterPumpDelayTime = boosterPumpDelayTime;
        }

        public Item() {
        }

        public void deepCopy(Item item) {
            startTime = item.startTime;
            endTime = item.endTime;
            voiceLength = item.voiceLength;
            voiceInterval = item.voiceInterval;
            boosterPumpDelayTime = item.boosterPumpDelayTime;
        }
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

    public Item getManItem() {
        return manItem;
    }

    public void setManItem(Item manItem) {
        this.manItem = manItem;
    }

    public Item getWomanItem() {
        return womanItem;
    }

    public void setWomanItem(Item womanItem) {
        this.womanItem = womanItem;
    }

    public Item getDisableItem() {
        return disableItem;
    }

    public void setDisableItem(Item disableItem) {
        this.disableItem = disableItem;
    }

    public String toJson() {
        return GsonUtil.toJson(this);
    }
}
