package jp.hayamiti.websocket;


import org.json.JSONObject;

public class CaptureListener extends Listener{
    public static final String CHANNEL = "CAPTURE";
    private final String LOG_TAG = "CAPTUREListener";
    public CaptureListener(){
        super();
        setChannel(CHANNEL);
    }
    /**
     * 単にサイズを表示するだけ
     * @param data
     */
    @Override
    protected void callback(JSONObject data){
        try {
        	System.out.print(LOG_TAG);
            System.out.println("get capture:" + data.getString("payload").length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
