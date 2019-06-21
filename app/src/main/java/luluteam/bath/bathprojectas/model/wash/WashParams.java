package luluteam.bath.bathprojectas.model.wash;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WashParams {
    //没用的字段
    public static final int FIXED_LIST_SIZE = 10;
    public static final int DEFAULT_RUNNING_TIME = 10;
    public static final int DEFAULT_INTERVAL_TIME = 10;
    public static final int MAX_TIME = 15;


    private String toiletId;
    private String usage;
    private String deviceType;

    @Deprecated
    private int frameIndex;

    /**
     * 固定为10个元素的list
     */
    private List<Group> list = new ArrayList<>(FIXED_LIST_SIZE);

    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<Group> getList() {
        return list;
    }

    public void setList(List<Group> list) {
        this.list = list;
    }

    public static class Group {
        public int id;
        public int runningTime;
        public int intervalTime;
        public boolean selected;

        public Group(int id, int runningTime, int intervalTime, boolean selected) {
            this.id = id;
            this.runningTime = runningTime;
            this.intervalTime = intervalTime;
            this.selected = selected;
        }

        public static Group getDefaultGroup(int id) {
            return new Group(id, DEFAULT_RUNNING_TIME, DEFAULT_INTERVAL_TIME, true);
        }

        public static void deepCopy(Group group1, Group group2) {
            group1.intervalTime = group2.intervalTime;
            group1.runningTime = group2.runningTime;
            group1.selected = group2.selected;
        }

        @Override
        public String toString() {
            return "Group{" +
                    "id=" + id +
                    ", runningTime=" + runningTime +
                    ", intervalTime=" + intervalTime +
                    ", selected=" + selected +
                    '}';
        }
    }

    /**
     * 将model转化为json
     *
     * @return
     */
    public String toJson() {
        return new Gson().toJson(this);
    }
}
