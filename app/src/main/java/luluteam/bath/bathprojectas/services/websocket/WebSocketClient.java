package luluteam.bath.bathprojectas.services.websocket;

import android.os.Handler;
import android.os.HandlerThread;

import java.net.URI;

import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import luluteam.bath.bathprojectas.constants.WebConstant;


/**
 * 在Manifest文件中的Application标签中，加入如下代码。否则无法正常使用
 * tools:replace="android:label"
 * <uses-sdk
 * android:minSdkVersion="21"
 * android:targetSdkVersion="23"
 * tools:overrideLibrary="io.crossbar.autobahn" />
 * <p>
 * Created by TC on 2017/11/03.
 */
public class WebSocketClient {


    public static WebSocketClient client = null;
    private URI uri;
    private int port;

    /**
     * 用于处理发送和接收消息的线程
     */
    private HandlerThread handlerThread;
    private Handler wsHandler;
    private WebSocketConnection wsConnect;

    private WebSocketClient() {

    }


    public static WebSocketClient getInstance() {
        if (client == null) {
            synchronized (WebConstant.class) {
                if (client == null) {
                    client = new WebSocketClient();
                }
            }
        }
        return client;
    }

    /**
     * 开启WebSocket
     */
    public void start(WebSocketConnectionHandler webSocketConnectionHandler) {
        try {
            uri = new URI(getURL());
            String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                System.err.println("Only WS(S) is supported.");
                throw new Exception();
            }
            port = uri.getPort() == -1 ? 80 : uri.getPort();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handlerThread = new HandlerThread("WebSocket Handler Thread");
        handlerThread.start();
        wsHandler = new Handler(handlerThread.getLooper());

        wsConnect = new WebSocketConnection();
        wsHandler.post(new Runnable() {
            @Override
            public void run() {
                connect(webSocketConnectionHandler);
            }
        });
    }

    /**
     * 关闭WebSocket
     */
    public void stop() {
        if (wsConnect != null) {
            wsConnect.sendClose();
            wsConnect = null;
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }
        wsHandler = null;
    }

    /**
     * 用于发送消息
     *
     * @param msg
     */
    public void sendMsg(final String msg) {
        if (!isConnected()) {
            return;
        }
        wsHandler.post(new Runnable() {
            @Override
            public void run() {
                wsConnect.sendMessage(msg);
            }
        });
    }

    /**
     * 发起连接的主体
     */
    private void connect(WebSocketConnectionHandler webSocketConnectionHandler) {
        if (wsConnect == null) {
            System.out.println("WebSocket Client has been stopped. Please invoke start() method to run WebSocket Client.");
            return;
        }
        try {
            wsConnect.connect(getURL(), webSocketConnectionHandler);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断线重连:
     * 如果连接突然中止，会调用onClose()方法。其中会触发reconnect。
     * 如果连接不成功，还会调用onClose()方法。然后继续触发reconnect
     */
    public void reconnect(WebSocketConnectionHandler webSocketConnectionHandler) {
        if (wsHandler != null && !isConnected()) {
            wsHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect(webSocketConnectionHandler);
                }
            }, 3000);
        }

    }

    /**
     * 判断是否处于连接状态
     *
     * @return
     */
    private boolean isConnected() {
        if (wsConnect != null) {
            return wsConnect.isConnected();
        }
        return false;
    }

    private String getURL() {
        return WebConstant.WEB_SOCKET;
    }
}
