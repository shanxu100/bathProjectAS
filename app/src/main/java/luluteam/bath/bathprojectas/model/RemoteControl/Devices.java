package luluteam.bath.bathprojectas.model.RemoteControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guan on 2018/1/5.
 */

public class Devices {

    private String usage;
    private List<Device> devices;

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    /**
     * 使用默认设备列表
     *
     * @param usage
     */
    public Devices(String usage) {
        this.usage = usage;
        this.devices = new ArrayList<>();
        //增加设备时需要同时修改AppConstant里头的内容
        this.devices.add(new Device("主灯"));//0
        this.devices.add(new Device("辅灯"));//1
        this.devices.add(new Device("抽空风机"));//2
        this.devices.add(new Device("音响"));//3
        this.devices.add(new Device("消毒灯"));//4
        this.devices.add(new Device("风幕机"));//5
        this.devices.add(new Device("门设备"));//6
        this.devices.add(new Device("光照(LUX)"));//7
        this.devices.add(new Device("一键冲洗"));//8
    }

    public Devices(String usage, List<Device> devices) {
        this.usage = usage;
        this.devices = devices;
    }

    public static final class Device {
        private String name;
        private boolean action = false;
        private boolean controlled = false;
        private int value = 0;

        public Device(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isAction() {
            return action;
        }

        public void setAction(boolean action) {
            this.action = action;
        }

        public boolean isControlled() {
            return controlled;
        }

        public void setControlled(boolean controlled) {
            this.controlled = controlled;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
