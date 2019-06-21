package luluteam.bath.bathprojectas.model.result;

/**
 * Created by Guan on 2018/1/14.
 */

public class CheckUpdateResult {
    private boolean result;
    private int versionCode;
    private String postfixURL;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getPostfixURL() {
        return postfixURL;
    }

    public void setPostfixURL(String postfixURL) {
        this.postfixURL = postfixURL;
    }
}
