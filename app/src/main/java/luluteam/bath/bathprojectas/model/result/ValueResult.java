package luluteam.bath.bathprojectas.model.result;


/**
 * @author Guan
 * @date Created on 2018/8/18
 */
public class ValueResult {

    private boolean result;
    private String toiletId;
    private int value;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
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
