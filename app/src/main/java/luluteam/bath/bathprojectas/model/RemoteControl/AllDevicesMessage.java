package luluteam.bath.bathprojectas.model.RemoteControl;

/**
 * 整体设备的运行记录
 * Created by TC on 2017/10/24.
 */

public class AllDevicesMessage {
//    private int value_D_lightDetector;
//    private boolean action_W_MainLight;
//    private int value_W_bodyDetector;
//    private int value_D_bodyDetector;
//    private String time;
//    private int value_M_bodyDetector;
//    private boolean action_M_AirPump;
//    private boolean action_D_Sterilamp;
//    private boolean action_M_Audio;
//    private int value_M_lightDetector;
//    private boolean action_W_SecondaryLight;
//    private boolean action_D_Audio;
//    private String toiletId;
//    private boolean action_W_AirPump;
//    private boolean action_W_Sterilamp;
//    private boolean action_C_VoiceGuide;
//    private boolean action_M_Sterilamp;
//    private boolean action_W_Audio;
//    private int value_W_lightDetector;
//    private boolean action_M_MainLight;
//    private boolean action_D_AirPump;
//    private boolean action_W_FloorWash;
//    private boolean action_D_SecondaryLight;
//    private boolean action_D_FloorWash;
//    private int flag;
//    private boolean action_D_WindMachine;
//    private boolean action_M_SecondaryLight;
//    private boolean action_M_WindMachine;
//    private boolean action_D_MainLight;
//    private boolean action_W_WindMachine;
//    private boolean action_M_FloorWash;
//    private boolean controlled;
//    private Integer value_C_ElecMeter;
//    private Integer value_C_WaterMeter;

    private String toiletId;
    private String time;
    private int flag;


    /**
     * M: man
     * W: woman
     * D: disabled
     * C: common
     */
    private boolean action_M_MainLight;
    private boolean action_M_SecondaryLight;
    private boolean action_M_AirPump;
    private boolean action_M_Audio;
    private boolean action_M_Sterilamp;
    private boolean action_M_FloorWash;
    private boolean action_M_WindMachine;
    private int value_M_lightDetector;
    private int value_M_bodyDetector;

    private boolean action_W_MainLight;
    private boolean action_W_SecondaryLight;
    private boolean action_W_AirPump;
    private boolean action_W_Audio;
    private boolean action_W_Sterilamp;
    private boolean action_W_FloorWash;
    private boolean action_W_WindMachine;
    private int value_W_lightDetector;
    private int value_W_bodyDetector;

    private boolean action_D_MainLight;
    private boolean action_D_SecondaryLight;
    private boolean action_D_AirPump;
    private boolean action_D_Audio;
    private boolean action_D_Sterilamp;
    private boolean action_D_FloorWash;

    private boolean action_D_WindMachine;
    private int value_D_lightDetector;
    private int value_D_bodyDetector;

    private boolean action_C_VoiceGuide;

    private int value_C_WaterMeter;
    private int value_C_ElecMeter;

    private boolean controlled;

    @Override
    public String toString() {
        return "AllDevicesMessage{" +
                "value_D_lightDetector=" + value_D_lightDetector +
                ", action_W_MainLight=" + action_W_MainLight +
                ", value_W_bodyDetector=" + value_W_bodyDetector +
                ", value_D_bodyDetector=" + value_D_bodyDetector +
                ", time='" + time + '\'' +
                ", value_M_bodyDetector=" + value_M_bodyDetector +
                ", action_M_AirPump=" + action_M_AirPump +
                ", action_D_Sterilamp=" + action_D_Sterilamp +
                ", action_M_Audio=" + action_M_Audio +
                ", value_M_lightDetector=" + value_M_lightDetector +
                ", action_W_SecondaryLight=" + action_W_SecondaryLight +
                ", action_D_Audio=" + action_D_Audio +
                ", toiletId='" + toiletId + '\'' +
                ", action_W_AirPump=" + action_W_AirPump +
                ", action_W_Sterilamp=" + action_W_Sterilamp +
                ", action_C_VoiceGuide=" + action_C_VoiceGuide +
                ", action_M_Sterilamp=" + action_M_Sterilamp +
                ", action_W_Audio=" + action_W_Audio +
                ", value_W_lightDetector=" + value_W_lightDetector +
                ", action_M_MainLight=" + action_M_MainLight +
                ", action_D_AirPump=" + action_D_AirPump +
                ", action_W_FloorWash=" + action_W_FloorWash +
                ", action_D_SecondaryLight=" + action_D_SecondaryLight +
                ", action_D_FloorWash=" + action_D_FloorWash +
                ", flag=" + flag +
                ", action_D_WindMachine=" + action_D_WindMachine +
                ", action_M_SecondaryLight=" + action_M_SecondaryLight +
                ", action_M_WindMachine=" + action_M_WindMachine +
                ", action_D_MainLight=" + action_D_MainLight +
                ", action_W_WindMachine=" + action_W_WindMachine +
                ", action_M_FloorWash=" + action_M_FloorWash +
                ", controlled=" + controlled +
                ", value_C_ElecMeter=" + value_C_ElecMeter +
                ", value_C_WaterMeter=" + value_C_WaterMeter +
                ", action_M_door=" + action_M_door +
                ", action_W_door=" + action_W_door +
                ", action_D_door=" + action_D_door +
                ", controlled_M_door=" + controlled_M_door +
                ", controlled_W_door=" + controlled_W_door +
                ", controlled_D_door=" + controlled_D_door +
                ", controlled_M_FloorWash=" + controlled_M_FloorWash +
                ", controlled_M_Audio=" + controlled_M_Audio +
                ", controlled_M_MainLight=" + controlled_M_MainLight +
                ", controlled_M_SecondaryLight=" + controlled_M_SecondaryLight +
                ", controlled_M_WindMachine=" + controlled_M_WindMachine +
                ", controlled_M_AirPump=" + controlled_M_AirPump +
                ", controlled_M_Sterilamp=" + controlled_M_Sterilamp +
                ", controlled_W_FloorWash=" + controlled_W_FloorWash +
                ", controlled_W_Audio=" + controlled_W_Audio +
                ", controlled_W_MainLight=" + controlled_W_MainLight +
                ", controlled_W_SecondaryLight=" + controlled_W_SecondaryLight +
                ", controlled_W_WindMachine=" + controlled_W_WindMachine +
                ", controlled_W_AirPump=" + controlled_W_AirPump +
                ", controlled_W_Sterilamp=" + controlled_W_Sterilamp +
                ", controlled_D_FloorWash=" + controlled_D_FloorWash +
                ", controlled_D_Audio=" + controlled_D_Audio +
                ", controlled_D_MainLight=" + controlled_D_MainLight +
                ", controlled_D_SecondaryLight=" + controlled_D_SecondaryLight +
                ", controlled_D_WindMachine=" + controlled_D_WindMachine +
                ", controlled_D_AirPump=" + controlled_D_AirPump +
                ", controlled_D_Sterilamp=" + controlled_D_Sterilamp +
                ", controlled_C_VoiceGuid=" + controlled_C_VoiceGuid +
                '}';
    }

    private boolean controlled_M_FloorWash;
    private boolean controlled_M_Audio;
    private boolean controlled_M_MainLight;
    private boolean controlled_M_SecondaryLight;
    private boolean controlled_M_WindMachine;
    private boolean controlled_M_AirPump;
    private boolean controlled_M_Sterilamp;

    private boolean controlled_W_FloorWash;
    private boolean controlled_W_Audio;
    private boolean controlled_W_MainLight;
    private boolean controlled_W_SecondaryLight;
    private boolean controlled_W_WindMachine;
    private boolean controlled_W_AirPump;
    private boolean controlled_W_Sterilamp;

    private boolean controlled_D_FloorWash;
    private boolean controlled_D_Audio;
    private boolean controlled_D_MainLight;
    private boolean controlled_D_SecondaryLight;
    private boolean controlled_D_WindMachine;
    private boolean controlled_D_AirPump;
    private boolean controlled_D_Sterilamp;

    private boolean controlled_C_VoiceGuid;

    private boolean action_M_door;
    private boolean action_W_door;
    private boolean action_D_door;

    private boolean controlled_M_door;
    private boolean controlled_W_door;
    private boolean controlled_D_door;


    public Integer getValue_C_ElecMeter() {
        return value_C_ElecMeter;
    }

    public void setValue_C_ElecMeter(Integer value_C_ElecMeter) {
        this.value_C_ElecMeter = value_C_ElecMeter;
    }

    public Integer getValue_C_WaterMeter() {
        return value_C_WaterMeter;
    }

    public void setValue_C_WaterMeter(Integer value_C_WaterMeter) {
        this.value_C_WaterMeter = value_C_WaterMeter;
    }

    public boolean isControlled_M_FloorWash() {
        return controlled_M_FloorWash;
    }

    public void setControlled_M_FloorWash(boolean controlled_M_FloorWash) {
        this.controlled_M_FloorWash = controlled_M_FloorWash;
    }

    public boolean isControlled_M_Audio() {
        return controlled_M_Audio;
    }

    public void setControlled_M_Audio(boolean controlled_M_Audio) {
        this.controlled_M_Audio = controlled_M_Audio;
    }

    public boolean isControlled_M_MainLight() {
        return controlled_M_MainLight;
    }

    public void setControlled_M_MainLight(boolean controlled_M_MainLight) {
        this.controlled_M_MainLight = controlled_M_MainLight;
    }

    public boolean isControlled_M_SecondaryLight() {
        return controlled_M_SecondaryLight;
    }

    public void setControlled_M_SecondaryLight(boolean controlled_M_SecondaryLight) {
        this.controlled_M_SecondaryLight = controlled_M_SecondaryLight;
    }

    public boolean isControlled_M_WindMachine() {
        return controlled_M_WindMachine;
    }

    public void setControlled_M_WindMachine(boolean controlled_M_WindMachine) {
        this.controlled_M_WindMachine = controlled_M_WindMachine;
    }

    public boolean isControlled_M_AirPump() {
        return controlled_M_AirPump;
    }

    public void setControlled_M_AirPump(boolean controlled_M_AirPump) {
        this.controlled_M_AirPump = controlled_M_AirPump;
    }

    public boolean isControlled_M_Sterilamp() {
        return controlled_M_Sterilamp;
    }

    public void setControlled_M_Sterilamp(boolean controlled_M_Sterilamp) {
        this.controlled_M_Sterilamp = controlled_M_Sterilamp;
    }

    public boolean isControlled_W_FloorWash() {
        return controlled_W_FloorWash;
    }

    public void setControlled_W_FloorWash(boolean controlled_W_FloorWash) {
        this.controlled_W_FloorWash = controlled_W_FloorWash;
    }

    public boolean isControlled_W_Audio() {
        return controlled_W_Audio;
    }

    public void setControlled_W_Audio(boolean controlled_W_Audio) {
        this.controlled_W_Audio = controlled_W_Audio;
    }

    public boolean isControlled_W_MainLight() {
        return controlled_W_MainLight;
    }

    public void setControlled_W_MainLight(boolean controlled_W_MainLight) {
        this.controlled_W_MainLight = controlled_W_MainLight;
    }

    public boolean isControlled_W_SecondaryLight() {
        return controlled_W_SecondaryLight;
    }

    public void setControlled_W_SecondaryLight(boolean controlled_W_SecondaryLight) {
        this.controlled_W_SecondaryLight = controlled_W_SecondaryLight;
    }

    public boolean isControlled_W_WindMachine() {
        return controlled_W_WindMachine;
    }

    public void setControlled_W_WindMachine(boolean controlled_W_WindMachine) {
        this.controlled_W_WindMachine = controlled_W_WindMachine;
    }

    public boolean isControlled_W_AirPump() {
        return controlled_W_AirPump;
    }

    public void setControlled_W_AirPump(boolean controlled_W_AirPump) {
        this.controlled_W_AirPump = controlled_W_AirPump;
    }

    public boolean isControlled_W_Sterilamp() {
        return controlled_W_Sterilamp;
    }

    public void setControlled_W_Sterilamp(boolean controlled_W_Sterilamp) {
        this.controlled_W_Sterilamp = controlled_W_Sterilamp;
    }

    public boolean isControlled_D_FloorWash() {
        return controlled_D_FloorWash;
    }

    public void setControlled_D_FloorWash(boolean controlled_D_FloorWash) {
        this.controlled_D_FloorWash = controlled_D_FloorWash;
    }

    public boolean isControlled_D_Audio() {
        return controlled_D_Audio;
    }

    public void setControlled_D_Audio(boolean controlled_D_Audio) {
        this.controlled_D_Audio = controlled_D_Audio;
    }

    public boolean isControlled_D_MainLight() {
        return controlled_D_MainLight;
    }

    public void setControlled_D_MainLight(boolean controlled_D_MainLight) {
        this.controlled_D_MainLight = controlled_D_MainLight;
    }

    public boolean isControlled_D_SecondaryLight() {
        return controlled_D_SecondaryLight;
    }

    public void setControlled_D_SecondaryLight(boolean controlled_D_SecondaryLight) {
        this.controlled_D_SecondaryLight = controlled_D_SecondaryLight;
    }

    public boolean isControlled_D_WindMachine() {
        return controlled_D_WindMachine;
    }

    public void setControlled_D_WindMachine(boolean controlled_D_WindMachine) {
        this.controlled_D_WindMachine = controlled_D_WindMachine;
    }

    public boolean isControlled_D_AirPump() {
        return controlled_D_AirPump;
    }

    public void setControlled_D_AirPump(boolean controlled_D_AirPump) {
        this.controlled_D_AirPump = controlled_D_AirPump;
    }

    public boolean isControlled_D_Sterilamp() {
        return controlled_D_Sterilamp;
    }

    public void setControlled_D_Sterilamp(boolean controlled_D_Sterilamp) {
        this.controlled_D_Sterilamp = controlled_D_Sterilamp;
    }

    public boolean isControlled() {
        return controlled;
    }

    public void setControlled(boolean controlled) {
        this.controlled = controlled;
    }

    public int getValue_D_lightDetector() {
        return value_D_lightDetector;
    }

    public void setValue_D_lightDetector(int value_D_lightDetector) {
        this.value_D_lightDetector = value_D_lightDetector;
    }

    public boolean isAction_W_MainLight() {
        return action_W_MainLight;
    }

    public void setAction_W_MainLight(boolean action_W_MainLight) {
        this.action_W_MainLight = action_W_MainLight;
    }

    public int getValue_W_bodyDetector() {
        return value_W_bodyDetector;
    }

    public void setValue_W_bodyDetector(int value_W_bodyDetector) {
        this.value_W_bodyDetector = value_W_bodyDetector;
    }

    public int getValue_D_bodyDetector() {
        return value_D_bodyDetector;
    }

    public void setValue_D_bodyDetector(int value_D_bodyDetector) {
        this.value_D_bodyDetector = value_D_bodyDetector;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getValue_M_bodyDetector() {
        return value_M_bodyDetector;
    }

    public void setValue_M_bodyDetector(int value_M_bodyDetector) {
        this.value_M_bodyDetector = value_M_bodyDetector;
    }

    public boolean isAction_M_AirPump() {
        return action_M_AirPump;
    }

    public void setAction_M_AirPump(boolean action_M_AirPump) {
        this.action_M_AirPump = action_M_AirPump;
    }

    public boolean isAction_D_Sterilamp() {
        return action_D_Sterilamp;
    }

    public void setAction_D_Sterilamp(boolean action_D_Sterilamp) {
        this.action_D_Sterilamp = action_D_Sterilamp;
    }

    public boolean isAction_M_Audio() {
        return action_M_Audio;
    }

    public void setAction_M_Audio(boolean action_M_Audio) {
        this.action_M_Audio = action_M_Audio;
    }

    public int getValue_M_lightDetector() {
        return value_M_lightDetector;
    }

    public void setValue_M_lightDetector(int value_M_lightDetector) {
        this.value_M_lightDetector = value_M_lightDetector;
    }

    public boolean isAction_W_SecondaryLight() {
        return action_W_SecondaryLight;
    }

    public void setAction_W_SecondaryLight(boolean action_W_SecondaryLight) {
        this.action_W_SecondaryLight = action_W_SecondaryLight;
    }

    public boolean isAction_D_Audio() {
        return action_D_Audio;
    }

    public void setAction_D_Audio(boolean action_D_Audio) {
        this.action_D_Audio = action_D_Audio;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public boolean isAction_W_AirPump() {
        return action_W_AirPump;
    }

    public void setAction_W_AirPump(boolean action_W_AirPump) {
        this.action_W_AirPump = action_W_AirPump;
    }

    public boolean isAction_W_Sterilamp() {
        return action_W_Sterilamp;
    }

    public void setAction_W_Sterilamp(boolean action_W_Sterilamp) {
        this.action_W_Sterilamp = action_W_Sterilamp;
    }

    public boolean isAction_C_VoiceGuide() {
        return action_C_VoiceGuide;
    }

    public void setAction_C_VoiceGuide(boolean action_C_VoiceGuide) {
        this.action_C_VoiceGuide = action_C_VoiceGuide;
    }

    public boolean isAction_M_Sterilamp() {
        return action_M_Sterilamp;
    }

    public void setAction_M_Sterilamp(boolean action_M_Sterilamp) {
        this.action_M_Sterilamp = action_M_Sterilamp;
    }

    public boolean isAction_W_Audio() {
        return action_W_Audio;
    }

    public void setAction_W_Audio(boolean action_W_Audio) {
        this.action_W_Audio = action_W_Audio;
    }

    public int getValue_W_lightDetector() {
        return value_W_lightDetector;
    }

    public void setValue_W_lightDetector(int value_W_lightDetector) {
        this.value_W_lightDetector = value_W_lightDetector;
    }

    public boolean isAction_M_MainLight() {
        return action_M_MainLight;
    }

    public void setAction_M_MainLight(boolean action_M_MainLight) {
        this.action_M_MainLight = action_M_MainLight;
    }

    public boolean isAction_D_AirPump() {
        return action_D_AirPump;
    }

    public void setAction_D_AirPump(boolean action_D_AirPump) {
        this.action_D_AirPump = action_D_AirPump;
    }

    public boolean isAction_W_FloorWash() {
        return action_W_FloorWash;
    }

    public void setAction_W_FloorWash(boolean action_W_FloorWash) {
        this.action_W_FloorWash = action_W_FloorWash;
    }

    public boolean isAction_D_SecondaryLight() {
        return action_D_SecondaryLight;
    }

    public void setAction_D_SecondaryLight(boolean action_D_SecondaryLight) {
        this.action_D_SecondaryLight = action_D_SecondaryLight;
    }

    public boolean isAction_D_FloorWash() {
        return action_D_FloorWash;
    }

    public void setAction_D_FloorWash(boolean action_D_FloorWash) {
        this.action_D_FloorWash = action_D_FloorWash;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isAction_D_WindMachine() {
        return action_D_WindMachine;
    }

    public void setAction_D_WindMachine(boolean action_D_WindMachine) {
        this.action_D_WindMachine = action_D_WindMachine;
    }

    public boolean isAction_M_SecondaryLight() {
        return action_M_SecondaryLight;
    }

    public void setAction_M_SecondaryLight(boolean action_M_SecondaryLight) {
        this.action_M_SecondaryLight = action_M_SecondaryLight;
    }

    public boolean isAction_M_WindMachine() {
        return action_M_WindMachine;
    }

    public void setAction_M_WindMachine(boolean action_M_WindMachine) {
        this.action_M_WindMachine = action_M_WindMachine;
    }

    public boolean isAction_D_MainLight() {
        return action_D_MainLight;
    }

    public void setAction_D_MainLight(boolean action_D_MainLight) {
        this.action_D_MainLight = action_D_MainLight;
    }

    public boolean isAction_W_WindMachine() {
        return action_W_WindMachine;
    }

    public void setAction_W_WindMachine(boolean action_W_WindMachine) {
        this.action_W_WindMachine = action_W_WindMachine;
    }

    public boolean isAction_M_FloorWash() {
        return action_M_FloorWash;
    }

    public void setAction_M_FloorWash(boolean action_M_FloorWash) {
        this.action_M_FloorWash = action_M_FloorWash;
    }

    public boolean isControlled_C_VoiceGuid() {
        return controlled_C_VoiceGuid;
    }

    public void setControlled_C_VoiceGuid(boolean controlled_C_VoiceGuid) {
        this.controlled_C_VoiceGuid = controlled_C_VoiceGuid;
    }

    public boolean isAction_M_door() {
        return action_M_door;
    }

    public void setAction_M_door(boolean action_M_door) {
        this.action_M_door = action_M_door;
    }

    public boolean isAction_W_door() {
        return action_W_door;
    }

    public void setAction_W_door(boolean action_W_door) {
        this.action_W_door = action_W_door;
    }

    public boolean isAction_D_door() {
        return action_D_door;
    }

    public void setAction_D_door(boolean action_D_door) {
        this.action_D_door = action_D_door;
    }

    public boolean isControlled_M_door() {
        return controlled_M_door;
    }

    public void setControlled_M_door(boolean controlled_M_door) {
        this.controlled_M_door = controlled_M_door;
    }

    public boolean isControlled_W_door() {
        return controlled_W_door;
    }

    public void setControlled_W_door(boolean controlled_W_door) {
        this.controlled_W_door = controlled_W_door;
    }

    public boolean isControlled_D_door() {
        return controlled_D_door;
    }

    public void setControlled_D_door(boolean controlled_D_door) {
        this.controlled_D_door = controlled_D_door;
    }
}

/**
 * objectiveQuestionsResult = new ObjectiveQuestionsResult();
 * objectiveQuestionsResult = new Gson().fromJson(result, ObjectiveQuestionsResult.class);
 */
