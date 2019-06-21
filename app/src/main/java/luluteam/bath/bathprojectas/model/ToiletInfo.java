package luluteam.bath.bathprojectas.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TC on 2018/4/16.
 */
public class ToiletInfo implements Serializable {
    private List<ToiletItem> toiletList;

    @Override
    public String toString() {
        return "ToiletInfo{" +
                "toiletList=" + toiletList +
                '}';
    }

    public List<ToiletItem> getToiletList() {
        return toiletList;
    }

    public void setToiletList(List<ToiletItem> toiletList) {
        this.toiletList = toiletList;
    }

    public static class ToiletItem implements Serializable {
        private String toiletId;
        private String cardId;
        private double waterAdjustedValue;
        private String province;
        private String city;
        private String county;
        private String detail;
        private String toiletType;
        private String companyName;
        private String nickname;

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        //是否在线
        private boolean online = false;
        /**
         * 新增采集卡板子的版本
         */
        private int version;

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getToiletId() {
            return toiletId;
        }

        public void setToiletId(String toiletId) {
            this.toiletId = toiletId;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public double getWaterAdjustedValue() {
            return waterAdjustedValue;
        }

        public void setWaterAdjustedValue(double waterAdjustedValue) {
            this.waterAdjustedValue = waterAdjustedValue;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getToiletType() {
            return toiletType;
        }

        public void setToiletType(String toiletType) {
            this.toiletType = toiletType;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public String toString() {
            return "ToiletItem{" +
                    "toiletId='" + toiletId + '\'' +
                    ", cardId='" + cardId + '\'' +
                    ", waterAdjustedValue=" + waterAdjustedValue +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", county='" + county + '\'' +
                    ", detail='" + detail + '\'' +
                    ", toiletType='" + toiletType + '\'' +
                    ", companyName='" + companyName + '\'' +
                    ", nickname='" + nickname + '\'' +
                    '}';
        }
    }
}
