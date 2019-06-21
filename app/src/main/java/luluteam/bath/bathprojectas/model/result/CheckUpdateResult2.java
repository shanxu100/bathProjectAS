package luluteam.bath.bathprojectas.model.result;

/**
 * Created by Administrator on 2018/4/18 0018.
 */

public class CheckUpdateResult2 {

    /**
     * code : 0
     * message :
     * data : {"buildBuildVersion":"1","downloadURL":"https://www.pgyer.com/app/installUpdate/ddfb3910b7411824244af1cde6ee4337?sig=%2BBDEzOWvL7tnYCNF%2FT7EHePj8F2wJNc0OYRY0CrIX0nPPRbErlCcN9YDzrJYsc%2BK","haveNewVersion":false,"buildVersionNo":"1","buildVersion":"1.0","buildShortcutUrl":"https://www.pgyer.com/PjdP","buildUpdateDescription":"","buildHaveNewVersion":true}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * buildBuildVersion : 1
         * downloadURL : https://www.pgyer.com/app/installUpdate/ddfb3910b7411824244af1cde6ee4337?sig=%2BBDEzOWvL7tnYCNF%2FT7EHePj8F2wJNc0OYRY0CrIX0nPPRbErlCcN9YDzrJYsc%2BK
         * haveNewVersion : false
         * buildVersionNo : 1
         * buildVersion : 1.0
         * buildShortcutUrl : https://www.pgyer.com/PjdP
         * buildUpdateDescription :
         * buildHaveNewVersion : true
         */

        private int buildBuildVersion;
        private String downloadURL;
        private int buildVersionNo;

        public int getBuildVersionNo() {
            return buildVersionNo;
        }

        public int getBuildBuildVersion() {
            return buildBuildVersion;
        }

        public String getDownloadURL() {
            return downloadURL;
        }

        public void setDownloadURL(String downloadURL) {
            this.downloadURL = downloadURL;
        }
    }
}
