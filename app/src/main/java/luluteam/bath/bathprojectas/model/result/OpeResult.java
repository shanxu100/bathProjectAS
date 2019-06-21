package luluteam.bath.bathprojectas.model.result;

/**
 * @author Guan
 * @date Created on 2018/5/14
 */
public class OpeResult {

    private boolean result;
    private String reason;
    private String fileId;
    private String toiletId;
    private int value;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
