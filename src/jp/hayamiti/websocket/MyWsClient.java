package jp.hayamiti.websocket;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class MyWsClient {
    final static String LOG_TAG = "MyWsClient";
    private static ArrayList<Listener> listeners = new ArrayList<Listener>();
    private static WebSocketClient cc;

    public MyWsClient(URI serverUri) {
        cc = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                MyWsClient.emit(MessageListener.CHANNEL, "Hello");
                MyLog.info(LOG_TAG, "new connection opened");
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                MyLog.info(LOG_TAG, "closed with exit code " + code + " additional info: " + reason);
            }

            @Override
            public void onMessage(String message) {
                MyLog.info(LOG_TAG, "received message: " + message);
                try {
                    JSONObject jsonData = new JSONObject(message);
                    // <送られてきたjsonのchannelに応じてイベント処理を行う>
                    for (int i = 0; i < listeners.size(); i++) {
                        // jsonデータとリスナーのchannelが同じとき
                        if (jsonData.getString("channel").equals(listeners.get(i).getChannel())) {
                            listeners.get(i).callback(jsonData);
                            break;
                        }
                    }
                    // </送られてきたjsonのchannelに応じてイベント処理を行う>
                } catch (Exception e) {
                    MyLog.error(LOG_TAG, "MyWebSock onMessage error:" + e);
                    MyLog.error(LOG_TAG, message);
                }
            }

            @Override
            public void onMessage(ByteBuffer message) {
                MyLog.info(LOG_TAG, "received ByteBuffer");
            }

            @Override
            public void onError(Exception ex) {
                MyLog.error(LOG_TAG, "an error occurred:" + ex);
            }
        };
    }

    public void connect() {
        MyLog.info(LOG_TAG, "connect");
        cc.connect();
    }

    public void disconnect() {
    	MyLog.info(LOG_TAG, "disconnected");
    	cc.close();
    }

    /**
     * 送信
     *
     * @param header  チャネル
     * @param payload データ
     */
    public static void emit(final String header, final String payload) {
        // 送信処理に時間がかかるかもしれないから、新たにスレッドを作る
        new Thread(new Runnable() {
            public void run() {
                try {

                    // <Json変換>
                    // urlアンセーフな文字を<文字名>に変換する.そのままだと例外が発生する.
                    String str = "{\"payload\":"
                            + payload
                            + ", \"channel\":" + header
                            + "}";
                    JSONObject jsonObject = new JSONObject(str);
                    // </Json変換>
                    // <送信>
                    cc.send(jsonObject.toString());
                    // </送信>
                    System.out.println("send:" + jsonObject.toString().length());

                } catch (Exception e) {
                    MyLog.error(LOG_TAG, e.toString());

                }
            }
        }).start();
    }

    /**
     * リスナー追加
     *
     * @param listener リスナー
     */
    public static void on(Listener listener) {
        listeners.add(listener);
    }

    /**
     * リスナー削除
     *
     * @param listener リスナー
     */
    public static void off(Listener listener) {
        listeners.remove(listener);
    }
}
