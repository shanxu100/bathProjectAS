package luluteam.bath.bathprojectas.video.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.LinkedList;
import java.util.List;

import luluteam.bath.bathprojectas.video.other.ParseHelper;

/**
 * @author Guan
 * @date Created on 2018/5/28
 */
public class CameraListResult {

    private String code;
    private String msg;
    private Page page;
    private List<Item> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    /**
     * 只获取 ChannelName 字段，组成一个List
     * ChannelName就是用户自己给摄像头起的名字
     *
     * @return
     */
    public List<String> getChannelNameList() {
        List<String> channelNames = new LinkedList<>();
        if (data == null || data.size() == 0) {
            return channelNames;
        }
        for (Item item : data) {
            channelNames.add(item.getChannelName());
        }
        return channelNames;
    }

    /**
     * 用于ListView的精简显示
     *
     * @return
     */
    public List<String> getFormattedInfoList() {
        List<String> list = new LinkedList<>();
        if (data == null || data.size() == 0) {
            return list;
        }
        StringBuilder sb = new StringBuilder();
        for (Item item : data) {
            sb.append("\n名称：  ").append(item.getChannelName()).append("\n\n");
            sb.append("序列号：  ").append(item.getDeviceSerial());
            sb.append("       ").append(ParseHelper.parseStatus(item.getStatus()));
//            sb.append("      ");
//            sb.append(ParseHelper.parseVideoLevel(item.getVideoLevel()));
            sb.append("      ").append(ParseHelper.parseIsEncrypt(item.getIsEncrypt()));

            sb.append("\n");
            list.add(sb.toString());
            sb.delete(0, sb.length());
        }
        return list;
    }


    /**
     * 设置部分字体的颜色
     * 用于测试而已
     *
     * @return
     */
    public List<SpannableStringBuilder> getAllInfoSSBList() {
        List<SpannableStringBuilder> list = new LinkedList<>();
        if (data == null || data.size() == 0) {
            return list;
        }
        for (Item item : data) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("\n名称：  ").append(item.getChannelName()).append("\n\n");
            sb.append(item.getDeviceSerial()).append("       ");
            int p1 = sb.length();
            String status = ParseHelper.parseStatus(item.getStatus());
            sb.append(status);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#009ad6"));
            sb.setSpan(colorSpan, p1 - 1, p1 + status.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            sb.append("      ");
//            sb.append(ParseHelper.parseVideoLevel(item.getVideoLevel()));
            sb.append("      ");
            sb.append(ParseHelper.parseIsEncrypt(item.getIsEncrypt()));

            sb.append("\n");
            list.add(sb);
        }
        return list;
    }


    //===============================================================

    /**
     *
     */
    public static class Page {
        private String total;
        private String page;
        private String size;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    /**
     *
     */
    public static class Item {
        private String deviceSerial;
        private int channelNo;
        /**
         * 自定义名称：规则  ToiletID-名称-男/女/残
         */
        private String channelName;
        private int status;
        private String isShared;
        private String picUrl;
        private int isEncrypt;
        private int videoLevel;

        public String getDeviceSerial() {
            return deviceSerial;
        }

        public void setDeviceSerial(String deviceSerial) {
            this.deviceSerial = deviceSerial;
        }

        public int getChannelNo() {
            return channelNo;
        }

        public void setChannelNo(int channelNo) {
            this.channelNo = channelNo;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getIsShared() {
            return isShared;
        }

        public void setIsShared(String isShared) {
            this.isShared = isShared;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public int getIsEncrypt() {
            return isEncrypt;
        }

        public void setIsEncrypt(int isEncrypt) {
            this.isEncrypt = isEncrypt;
        }

        public int getVideoLevel() {
            return videoLevel;
        }

        public void setVideoLevel(int videoLevel) {
            this.videoLevel = videoLevel;
        }
    }


}
