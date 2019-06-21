package luluteam.bath.bathprojectas.model.result;

/**
 * @author Guan
 * @date Created on 2018/1/8
 */
public class LoginResult {
    private boolean result;
    private boolean singleLogin;
    private String reason;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isSingleLogin() {
        return singleLogin;
    }

    public void setSingleLogin(boolean singleLogin) {
        this.singleLogin = singleLogin;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
