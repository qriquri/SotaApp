package jp.hayamiti.network;


import org.json.JSONException;
import org.json.JSONObject;

public class MessageListener extends Listener{
    public static final String CHANNEL = "MESSAGE";
    private final String LOG_TAG = "MessageListener";
    public MessageListener(){
            super();
            setChannel(CHANNEL);
    }


    @Override
    protected void callback(JSONObject data){
        try {
        	System.out.print(LOG_TAG);
        	System.out.println("get message:" + data.getString("payload"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
