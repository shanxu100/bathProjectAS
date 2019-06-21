package luluteam.bath.bathprojectas.model;

import java.io.Serializable;

public class RunDeviceDetail implements Serializable {
    private String deviceName;
    private boolean work;

    public RunDeviceDetail(String deviceName, boolean work) {
        this.deviceName = deviceName;
        this.work = work;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isWork() {
        return work;
    }

    public void setState(boolean work) {
        this.work = work;
    }
}
