package luluteam.bath.bathprojectas.model.pit;

import java.util.List;

/**
 * 485设备 蹲位/便池 整体状态数据
 *
 * @author Guan
 * @date Created on 2018/6/13
 */

public class Bus485Pit {

    public static final int MAX_MAN_WOMAN_PIT = 60;
    public static final int MAX_MAN_STAND = 30;
    public static final int MAX_DISABLED_PIT = 10;
    public static final int MAX_WASH = 10;
    public static final int MAX_MAN_GAS = 7;
    public static final int MAX_WOMAN_GAS = 6;
    public static final int MAX_DISABLED_GAS = 7;


    //TODO 根据协议，重新定义结构

    private String toiletId;

    private String time;

    private long timestamp;


    /**
     * index=0 : 请求公共数据
     * index=1 ： 56+56 有人无人、正常异常、清洁度等级、气味等级
     * index=2  ：蹲位详情
     * index=3 ： 气味详情
     */
    private int index;

    /**
     * 公共数据
     */
    public ItemCommon itemCommon;

    /**
     * 以下9个list分别对应index=1 的数据
     */
    public List<Item> manList;
    public List<Item> womanList;
    public List<Item> disabledList;

    public List<Item> manWashList;
    public List<Item> womanWashList;
    public List<Item> disabledWashList;

    public List<Item> manGasList;
    public List<Item> womanGasList;
    public List<Item> disabledGasList;

    //清洁度，气味浓度字符串
    public String gasLevelStr = "优";
    public String cleanessLevelStr = "优";

    /**
     * 对应index=2的坑位详情，表示是否有污物，以及污物类型是什么
     */
    public List<ItemPit> manDetailList;
    public List<ItemPit> womanDetailList;
    public List<ItemPit> disabledDetailList;

    /**
     * 对应index=3的气体探测器详情，表示各种气味类型数据
     */
    public List<ItemGas> manGasDetailList;
    public List<ItemGas> womanGasDetailList;
    public List<ItemGas> disabledGasDetailList;


    public String getToiletId() {
        return toiletId;
    }

    public void setToiletId(String toiletId) {
        this.toiletId = toiletId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ItemCommon getItemCommon() {
        return itemCommon;
    }

    public void setItemCommon(ItemCommon itemCommon) {
        this.itemCommon = itemCommon;
    }

    public List<Item> getManList() {
        return manList;
    }

    public void setManList(List<Item> manList) {
        this.manList = manList;
    }

    public List<Item> getWomanList() {
        return womanList;
    }

    public void setWomanList(List<Item> womanList) {
        this.womanList = womanList;
    }

    public List<Item> getDisabledList() {
        return disabledList;
    }

    public void setDisabledList(List<Item> disabledList) {
        this.disabledList = disabledList;
    }


    public static class Item {
        /**
         * 有人没人：true有人，false无人
         */
        public boolean used = false;

        /**
         * 是否正常工作：true正常，false异常
         */
        public boolean work = true;

        /**
         * 0：蹲位
         * 1：便池
         * 2: 冲水设备
         * 3: 气体传感器设备
         */
        public int type = 0;
        public int save;

        /**
         * 清洁度等级
         * 0 - 25% - 50% - 75%
         * <p>
         * -1：无效
         */
        public int cleaness = -1;

        /**
         * 气味等级
         * 0 - 25% - 50% - 75%
         * <p>
         * -1：无效
         */
        public int gasLevel = -1;

        public static Item newEmptyInstance(int type) {
            Item item = new Item();
            item.type = type;
            return item;
        }
    }

    /**
     * 坑位详情数据
     */
    public static class ItemPit extends Item {


        /**
         * 污物位置：
         * -1 数据无效
         * 0x00 -> 0 无污物
         * 0x01 -> 1 坑内有污物
         * 0x02 -> 2 坑外有污物
         */
        public int ordurePoision = -1;
        /**
         * 污物类型：
         * -1 数据无效
         * 0x00 -> 0 无污物
         * 0x01 -> 1 纸屑
         * 0x02 -> 2 烟头
         */
        public int ordureType = -1;


        public static ItemPit newEmptyInstance(int type) {
            ItemPit itemPit = new ItemPit();
            itemPit.type = type;
            return itemPit;
        }

        /**
         * 更新值：深度复制
         *
         * @param itemPit
         */
        public void update(ItemPit itemPit) {
            this.cleaness = itemPit.cleaness;
            this.used = itemPit.used;
            this.work = itemPit.work;
        }

    }


    /**
     * 气体探测器详情
     */
    public static class ItemGas extends Item {
        public int temperature;
        public int humidity;
        public int NH3;
        public int H2S;
        public int value;

        public static ItemGas newEmptyInstance(int type) {
            ItemGas itemGas = new ItemGas();
            itemGas.type = type;
            return itemGas;
        }
    }


    /**
     * 对应 10 Byte 的公共数据部分
     */
    public static class ItemCommon {
        public int version;
        public int hostId;

        public int numManSit;
        public int numManStand;

        public int numWomanSit;

        public int numDisabledSit;

        public int numDisabledStand;

        public int numManGas;
        public int numWomanGas;
        public int numDisabledGas;

        public int numManPitWash;
        public int numWomanPitWash;
        public int numDisabledPitWash;
        public int numManRoadWash;
        public int numWomanRoadWash;
        public int numDisabledRoadWash;
        /**
         * 保留，无意义
         */
        public int save;

        public ItemCommon() {

        }

        public static ItemCommon buildEmptyInstance() {
            return new ItemCommon();
        }

        @Override
        public String toString() {
            return "ItemCommon{" +
                    "numManSit=" + numManSit +
                    ", numManStand=" + numManStand +
                    ", numWomanSit=" + numWomanSit +
                    ", numDisabledSit=" + numDisabledSit +
                    ", numDisabledStand=" + numDisabledStand +
                    ", numManGas=" + numManGas +
                    ", numWomanGas=" + numWomanGas +
                    ", numDisabledGas=" + numDisabledGas +
                    ", numManPitWash=" + numManPitWash +
                    ", numWomanPitWash=" + numWomanPitWash +
                    ", numDisabledPitWash=" + numDisabledPitWash +
                    ", numManRoadWash=" + numManRoadWash +
                    ", numWomanRoadWash=" + numWomanRoadWash +
                    ", numDisabledRoadWash=" + numDisabledRoadWash +
                    '}';
        }
    }


}
