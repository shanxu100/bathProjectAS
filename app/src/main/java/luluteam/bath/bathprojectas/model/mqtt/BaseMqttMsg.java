package luluteam.bath.bathprojectas.model.mqtt;

import com.google.gson.Gson;

/**
 * @author Guan
 * @date 2018/1/8
 */

public class BaseMqttMsg {
    public String type = "";


    public final static class MqttType {
        public static final String EXIT_APP = "exitApp";
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
