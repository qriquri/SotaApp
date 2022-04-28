package jp.hayamiti.websocket;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageListener extends Listener{
    public static final String CHANNEL = "MESSAGE";
    public MessageListener(){
            super();
            setChannel(CHANNEL);
    }
    @Override
    /**
     * メッセージを受け取ったら送信する
     */
    protected void callback(JSONObject data){
        try {
            System.out.println("get message:" + data.getString("payload"));
            MyWsClient.emit(CHANNEL, data.getString("payload"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}