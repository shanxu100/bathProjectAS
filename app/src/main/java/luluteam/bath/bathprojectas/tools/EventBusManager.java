package luluteam.bath.bathprojectas.tools;

import org.greenrobot.eventbus.EventBus;

import luluteam.bath.bathprojectas.model.AllDeviceWorkInfo;
import luluteam.bath.bathprojectas.model.AutoWashTimer2Info;
import luluteam.bath.bathprojectas.model.ParamsInfo;
import luluteam.bath.bathprojectas.model.RemoteControl.AllDevicesMessage;
import luluteam.bath.bathprojectas.model.RemoteControl.DeviceMessage;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.result.LockStateResult;
import luluteam.bath.bathprojectas.model.wash.WashParams;

/**
 * Created by Guan on 2017/12/9.
 */

public class EventBusManager {
    public enum INSTANCE_NAME {RemoteControl, Params, Pit, Broadcast, wash, autoWash, work_485}

    public enum MsgType {fromServer, fromWebSocket, fromMqtt}

    private static EventBus remoteControl_EB = new EventBus();
    private static EventBus params_EB = new EventBus();
    private static EventBus broadcast_EB = new EventBus();
    private static EventBus pit_EB = new EventBus();
    private static EventBus wash_EB = new EventBus();
    private static EventBus autoWash_EB = new EventBus();
    private static EventBus work_485_EB = new EventBus();

    /**
     * 根据名称获取EventBus实例
     *
     * @param instanceName
     * @return
     */
    public static EventBus getInstance(INSTANCE_NAME instanceName) {
        if (instanceName == INSTANCE_NAME.RemoteControl) {
            return remoteControl_EB;
        } else if (instanceName == INSTANCE_NAME.Params) {
            return params_EB;
        } else if (instanceName == INSTANCE_NAME.Broadcast) {
            return broadcast_EB;
        } else if (instanceName == INSTANCE_NAME.Pit) {
            return pit_EB;
        } else if (instanceName == INSTANCE_NAME.wash) {
            return wash_EB;
        } else if (instanceName == INSTANCE_NAME.autoWash) {
            return autoWash_EB;
        } else if (instanceName == INSTANCE_NAME.work_485) {
            return work_485_EB;
        } else {
            return EventBus.getDefault();
        }
    }

    public static final class EventBusMsg {
        public final MsgType msgType;

        private int flag;
        private AllDevicesMessage allDevicesMessage;
        private DeviceMessage deviceMessage;
        private ParamsInfo paramsInfo;
        private LockStateResult lockStateResult;
        private Bus485Pit bus485PitResult;
        private WashParams washParams;

        private AutoWashTimer2Info autoWashTimer2Info;
        private AllDeviceWorkInfo allDeviceWorkInfo;

        public AutoWashTimer2Info getAutoWashTimer2Info() {
            return autoWashTimer2Info;
        }

        public void setAutoWashTimer2Info(AutoWashTimer2Info autoWashTimer2Info) {
            this.autoWashTimer2Info = autoWashTimer2Info;
        }


        public WashParams getWashParams() {
            return washParams;
        }

        public void setWashParams(WashParams washParams) {
            this.washParams = washParams;
        }

        private boolean action;

        public EventBusMsg(MsgType msgType) {
            this.msgType = msgType;
        }

        public MsgType getMsgType() {
            return msgType;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public AllDevicesMessage getAllDevicesMessage() {
            return allDevicesMessage;
        }

        public void setAllDevicesMessage(AllDevicesMessage allDevicesMessage) {
            this.allDevicesMessage = allDevicesMessage;
        }

        public DeviceMessage getDeviceMessage() {
            return deviceMessage;
        }

        public void setDeviceMessage(DeviceMessage deviceMessage) {
            this.deviceMessage = deviceMessage;
        }

        public ParamsInfo getParamsInfo() {
            return paramsInfo;
        }

        public void setParamsInfo(ParamsInfo paramsInfo) {
            this.paramsInfo = paramsInfo;
        }

        public LockStateResult getLockStateResult() {
            return lockStateResult;
        }

        public void setLockStateResult(LockStateResult lockStateResult) {
            this.lockStateResult = lockStateResult;
        }

        public Bus485Pit getBus485PitResult() {
            return bus485PitResult;
        }

        public void setBus485PitResult(Bus485Pit bus485PitResult) {
            this.bus485PitResult = bus485PitResult;
        }


        public AllDeviceWorkInfo getAllDeviceWorkInfo() {
            return allDeviceWorkInfo;
        }

        public void setAllDeviceWorkInfo(AllDeviceWorkInfo allDeviceWorkInfo) {
            this.allDeviceWorkInfo = allDeviceWorkInfo;
        }

        public boolean isAction() {
            return action;
        }

        public void setAction(boolean action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return "EventBusMsg{" +
                    "msgType=" + msgType +
                    ", flag=" + flag +
                    ", allDevicesMessage=" + allDevicesMessage +
                    ", deviceMessage=" + deviceMessage +
                    ", paramsInfo=" + paramsInfo +
                    ", action=" + action +
                    '}';
        }
    }

}
