package luluteam.bath.bathprojectas.model.pit;

import java.util.List;

public class ToiletDeviceResult {
    private boolean result;
    private List<ToiletDevice> dataList;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<ToiletDevice> getDataList() {
        return dataList;
    }

    public void setDataList(List<ToiletDevice> dataList) {
        this.dataList = dataList;
    }
}
