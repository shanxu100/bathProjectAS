package luluteam.bath.bathprojectas.model.pit;

public class ToiletDevice {
    private String toiletId;
    private String time;
    private long timestamp;
    private Bus485Pit.ItemCommon itemCommon;


    public ToiletDevice() {
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Bus485Pit.ItemCommon getItemCommon() {
        return itemCommon;
    }

    public void setItemCommon(Bus485Pit.ItemCommon itemCommon) {
        this.itemCommon = itemCommon;
    }
}
