package luluteam.bath.bathprojectas.model;

import java.util.ArrayList;
import java.util.List;

public class AllDeviceWorkInfo {
    public enum VERSION {VERSION_1, VERSION_2}

    private String toiletId;
    private String toiletName;
    private DeviceValue manDeviceValue;
    private DeviceValue womanDeviceValue;
    private DeviceValue disableDeviceValue;
    private CommonDeviceValue commonDeviceValue;

    private VERSION version;
    /**
     * flag用来指代整个设备是否有故障，false代表没故障，true代表有故障
     */
    private boolean stateFlag;

    public String getToiletName() {
        return toiletName;
    }

    public void setToiletName(String toiletName) {
        this.toiletName = toiletName;
    }

    public VERSION getVersion() {
        return version;
    }

    public void setVersion(VERSION version) {
        this.version = version;
    }

    public boolean isStateFlag() {
        return stateFlag;
    }

    public void setStateFlag(boolean stateFlag) {
        this.stateFlag = stateFlag;
    }

    @Override
    public String toString() {
        return "{" +
                "toiletId:" + toiletId + " toiletName:" + toiletName + " version:" + version + " stateFlag:" + stateFlag + "}";
    }

    public static class DeviceValue {
        public boolean mainLightWork;
        public boolean secondaryLightWork;
        public boolean airPumpWork;
        @Deprecated
        public boolean audioWork;
        public boolean sterilampWork;
        @Deprecated
        public boolean floorWashWork;
        public boolean voiceGuideWork;
        /**
         * 冲洗
         */
        public boolean fastWashWork;
        public boolean windMachineWork;
        public boolean lightDetectorWork;
        public boolean bodyDetectorWork;
        public boolean systemClockWork;
        public boolean doorWork;
        public boolean boosterPumpWork;
        public boolean modBusWork;
    }

    public static class CommonDeviceValue {
        public boolean waterMeter;
        public boolean elecMeter;
        public boolean pcDevice;

        /**
         * 音箱
         */
        public boolean voiceBox;
        /**
         * 增压泵
         */
        public boolean boosterPump;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public DeviceValue getManDeviceValue() {
        return manDeviceValue;
    }

    public void setManDeviceValue(DeviceValue manDeviceValue) {
        this.manDeviceValue = manDeviceValue;
    }

    public DeviceValue getWomanDeviceValue() {
        return womanDeviceValue;
    }

    public void setWomanDeviceValue(DeviceValue womanDeviceValue) {
        this.womanDeviceValue = womanDeviceValue;
    }

    public DeviceValue getDisableDeviceValue() {
        return disableDeviceValue;
    }

    public void setDisableDeviceValue(DeviceValue disableDeviceValue) {
        this.disableDeviceValue = disableDeviceValue;
    }

    public CommonDeviceValue getCommonDeviceValue() {
        return commonDeviceValue;
    }

    public void setCommonDeviceValue(CommonDeviceValue commonDeviceValue) {
        this.commonDeviceValue = commonDeviceValue;
    }

    public static List<RunDeviceDetail> TO_DETAIL(AllDeviceWorkInfo.DeviceValue value, AllDeviceWorkInfo.CommonDeviceValue commonDeviceValue) {
        List<RunDeviceDetail> deviceDetails = new ArrayList<>();
        deviceDetails.add(new RunDeviceDetail("主灯", value.mainLightWork));
        deviceDetails.add(new RunDeviceDetail("辅灯", value.secondaryLightWork));
        deviceDetails.add(new RunDeviceDetail("抽空风机", value.airPumpWork));
//        deviceDetails.add(new RunDeviceDetail("音响", value.audioWork));
        deviceDetails.add(new RunDeviceDetail("消毒灯", value.sterilampWork));
//        deviceDetails.add(new RunDeviceDetail("地面冲洗", value.floorWashWork));

        deviceDetails.add(new RunDeviceDetail("语音提示", value.voiceGuideWork));
        deviceDetails.add(new RunDeviceDetail("冲洗", value.fastWashWork));
        deviceDetails.add(new RunDeviceDetail("风幕机", value.windMachineWork));
        deviceDetails.add(new RunDeviceDetail("光照探头", value.lightDetectorWork));
        deviceDetails.add(new RunDeviceDetail("人体感应探头", value.bodyDetectorWork));
        deviceDetails.add(new RunDeviceDetail("系统时钟", value.systemClockWork));
        deviceDetails.add(new RunDeviceDetail("门设备", value.doorWork));

//        deviceDetails.add(new RunDeviceDetail("增压泵设备", value.boosterPumpWork));
//        deviceDetails.add(new RunDeviceDetail("PC MOBUS设备", value.modBusWork));
        deviceDetails.add(new RunDeviceDetail("水表", commonDeviceValue.waterMeter));
        deviceDetails.add(new RunDeviceDetail("电表", commonDeviceValue.elecMeter));
        deviceDetails.add(new RunDeviceDetail("PC设备", commonDeviceValue.pcDevice));
        deviceDetails.add(new RunDeviceDetail("音箱", commonDeviceValue.voiceBox));
        deviceDetails.add(new RunDeviceDetail("增压泵", commonDeviceValue.boosterPump));


        return deviceDetails;
    }

}
