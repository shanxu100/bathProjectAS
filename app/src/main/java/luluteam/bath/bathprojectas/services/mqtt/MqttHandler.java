package luluteam.bath.bathprojectas.services.mqtt;


import android.util.Log;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import luluteam.bath.bathprojectas.app.App;
import luluteam.bath.bathprojectas.model.mqtt.BaseMqttMsg;
import luluteam.bath.bathprojectas.tools.EventBusManager;

/**
 * @author Guan
 * @date Created on 2017/11/30
 */
public class MqttHandler {
    private static final String TAG = "MqttHandler";


    private MqttHandler() {

    }

    /**
     * @param connected
     */
    public static void onNetwork(boolean connected) {
        EventBus eventBus = EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast);
        EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromMqtt);
        msg.setAction(connected);
        eventBus.post(msg);
    }


    /**
     * 处理mqtt消息
     *
     * @param json
     */
    public static void onMessage(String json) {
        if (StringUtils.isEmpty(json)) {
            Log.i(TAG, "mqtt on Message:  空数据");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getString("type").equals(BaseMqttMsg.MqttType.EXIT_APP)) {
                //退出App
                Log.i(TAG, "单点登录，退出APP");
                App.Logout();
            } else {
                Log.e(TAG, "未知数据：" + json);
            }
        } catch (JSONException e) {
            System.out.println("mqtt异常json:" + json);
            e.printStackTrace();
        }
    }


}
