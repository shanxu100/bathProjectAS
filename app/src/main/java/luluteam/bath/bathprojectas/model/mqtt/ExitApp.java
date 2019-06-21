package luluteam.bath.bathprojectas.model.mqtt;

/**
 * @author Guan
 * @date 2018/1/8
 */

public class ExitApp extends BaseMqttMsg {

    public ExitApp() {
        type = MqttType.EXIT_APP;
    }
}
