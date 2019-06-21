package luluteam.bath.bathprojectas.model.RemoteControl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TC on 2017/12/17.
 */

public class RemoteCtrlDevices {
    private ArrayList<String> devicesNames = new ArrayList<>();//设备名称
    private String usage;
    private HashMap<String, Integer> devicesStatus;//设备状态
    private HashMap<String, Boolean> devicesIsCtrl;//设备控制子开关

    private RemoteCtrlDevices() {
    }

    public RemoteCtrlDevices(String usage) {
        this.usage = usage;
    }

    public ArrayList<String> getDeviceNames() {
        return devicesNames;
    }

    public void setDeviceNames(ArrayList<String> deviceNames) {
        this.devicesNames.clear();
        this.devicesNames.addAll(deviceNames);
    }

    public ArrayList<String> getDevicesNames() {
        return devicesNames;
    }

    public void setDevicesNames(ArrayList<String> devicesNames) {
        this.devicesNames = devicesNames;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public HashMap<String, Integer> getDevicesStatus() {
        return devicesStatus;
    }

    public void setDevicesStatus(HashMap<String, Integer> devicesStatus) {
        this.devicesStatus = devicesStatus;
    }

    public void setDevicesStatus(HashMap<String, Integer> devicesStatus, HashMap<String, Boolean> devicesIsCtrl) {
        this.devicesStatus = devicesStatus;
        this.devicesIsCtrl = devicesIsCtrl;
    }

    public HashMap<String, Boolean> getDevicesIsCtrl() {
        return devicesIsCtrl;
    }

    public void setDevicesIsCtrl(HashMap<String, Boolean> devicesIsCtrl) {
        this.devicesIsCtrl = devicesIsCtrl;
    }
}
