package luluteam.bath.bathprojectas.services.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import luluteam.bath.bathprojectas.constants.APPConstant;
import luluteam.bath.bathprojectas.constants.WebConstant;
import luluteam.bath.bathprojectas.model.AllDeviceWorkInfo;
import luluteam.bath.bathprojectas.model.AutoWashTimer2Info;
import luluteam.bath.bathprojectas.model.ParamsInfo;
import luluteam.bath.bathprojectas.model.RemoteControl.AllDevicesMessage;
import luluteam.bath.bathprojectas.model.RemoteControl.DeviceMessage;
import luluteam.bath.bathprojectas.model.pit.Bus485Pit;
import luluteam.bath.bathprojectas.model.result.LockStateResult;
import luluteam.bath.bathprojectas.model.wash.WashParams;
import luluteam.bath.bathprojectas.tools.EventBusManager;


/**
 * Created by TC on 2017/12/8.
 */

public class WebSocketService extends Service {

    private static String TAG = "WebSocketService";

    @Override
    public void onDestroy() {
        Log.i(TAG, "WebSocketService onDestroy");
        WebSocketClient.getInstance().stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "WebSocketService onStartCommand");

        WebSocketClient.getInstance().start(new WebSocketConnectionHandler() {
            @Override
            public void onMessage(String payload) {
                super.onMessage(payload);
                Log.i(TAG, "Websocket message:\n" + payload);
                int flag = -1;
                try {
                    flag = new JSONObject(payload).getInt("flag");
                    Log.i(TAG, "flag=" + flag);
                } catch (JSONException e) {
//                    e.printStackTrace();
                }
                if (flag == -1) {
                    return;
                } else if (flag == 1) {
                    Log.i(TAG, "整体状态记录");
                    AllDevicesMessage allDevicesMessage = new Gson().fromJson(payload, AllDevicesMessage.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setAllDevicesMessage(allDevicesMessage);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).post(msg);
                } else if (flag == 0) {
                    Log.i(TAG, "设备运行记录");
                    DeviceMessage deviceMessage = new Gson().fromJson(payload, DeviceMessage.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setDeviceMessage(deviceMessage);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).post(msg);
                } else if (flag == 2) {
                    Log.i(TAG, "参数阈值记录");
                    ParamsInfo paramsInfo = new Gson().fromJson(payload, ParamsInfo.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setParamsInfo(paramsInfo);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Params).post(msg);
                } else if (flag == 3) {
                    Log.i(TAG, "厕所锁的状态变化记录");
                    LockStateResult lockStateResult = new Gson().fromJson(payload, LockStateResult.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setLockStateResult(lockStateResult);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.RemoteControl).post(msg);
                } else if (flag == 4) {
                    Log.i(TAG, "厕所 蹲位 记录");
                    Bus485Pit bus485Pit = new Gson().fromJson(payload, Bus485Pit.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setBus485PitResult(bus485Pit);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Pit).post(msg);
                } else if (flag == 5) {
                    Log.i(TAG, "冲水参数数据记录");
                    WashParams washParams = new Gson().fromJson(payload, WashParams.class);
                    Log.e(TAG, washParams.toJson());
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setWashParams(washParams);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.wash).post(msg);
                } else if (flag == 6) {
                    Log.i(TAG, "自动冲水定时器参数数据记录");
                    AutoWashTimer2Info autoWashTimerInfo = new Gson().fromJson(payload, AutoWashTimer2Info.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setAutoWashTimer2Info(autoWashTimerInfo);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.autoWash).post(msg);
                } else if (flag == 7) {
                    Log.i(TAG, "所有设备工作状态数据记录");
                    AllDeviceWorkInfo allDeviceWorkInfo = new Gson().fromJson(payload, AllDeviceWorkInfo.class);
                    EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromServer);
                    msg.setFlag(flag);
                    msg.setAllDeviceWorkInfo(allDeviceWorkInfo);
                    EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.work_485).post(msg);
                } else {
                    Log.e(TAG, "websocket接收到未知记录");
                }
            }

            @Override
            public void onConnect(ConnectionResponse response) {
                super.onConnect(response);
                String toiletId = APPConstant.TOILETID;
                if (!StringUtils.isEmpty(toiletId)) {
                    WebSocketClient.getInstance().sendMsg(toiletId);
                    Log.i(TAG, "WebSocket 发送ToiletId:" + toiletId);
                }
                Log.e(TAG, "WebSocket on connect");
                //通知 WebSocket 已连接
                EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromWebSocket);
                msg.setAction(true);
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).post(msg);
            }

            @Override
            public void onClose(int code, String reason) {
                super.onClose(code, reason);
                Log.e(TAG, "WebSocket on close====" + reason + "  " + WebConstant.WEB_SOCKET);
                WebSocketClient.getInstance().reconnect(this);
                //通知 WebSocket 断开
                EventBusManager.EventBusMsg msg = new EventBusManager.EventBusMsg(EventBusManager.MsgType.fromWebSocket);
                msg.setAction(false);
                EventBusManager.getInstance(EventBusManager.INSTANCE_NAME.Broadcast).post(msg);
            }
        });
        return START_STICKY;
    }
}
