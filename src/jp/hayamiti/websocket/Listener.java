package jp.hayamiti.websocket;

import org.json.JSONObject;

abstract public class Listener {
    // listener のチャネル. これでリスナーを見分ける
    private String channel;

    /**
     * 子クラスのコンストラクタ内でこれを呼び出し、channelを指定する.こうしないとchannelがnullになる
     * @param ch channel名
     */
    public void setChannel(String ch){
        channel = ch;
    }

    public String getChannel(){
        return channel;
    }

    /**
     * リスナーの処理
     * @param data 送られてくるjsonデータ
     */
    abstract protected void callback(JSONObject data);
}