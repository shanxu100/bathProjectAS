package luluteam.bath.bathprojectas.model.result;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author Guan
 * @date Created on 2018/4/13
 */
public class FindFileResult {

    private boolean result;
    private List<FileInfo> dataList;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<FileInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<FileInfo> dataList) {
        this.dataList = dataList;
    }

    public static class FileInfo {
        private String fileId;
        private String toiletId;
        private String name;
        private String fullName;
        private String type;
        private long size;


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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }


        public String toJson() {
            return new Gson().toJson(this);
        }
    }

}
