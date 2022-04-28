package jp.hayamiti.websocket;

	import org.json.JSONObject;

import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;

public class FindNameListener extends Listener {
    public static final String CHANNEL = "FIND_NAME";
    private final String LOG_TAG = "FindNameListener";
    public FindNameListener(){
        super();
        setChannel(CHANNEL);
    }
    /**
     *
     * @param data
     */
    @Override
    protected void callback(JSONObject data){
        try {
        	MyLog.info(LOG_TAG, data.getJSONObject("payload").toString());
        	final boolean err = data.getJSONObject("payload").getBoolean("err");
        	if(err) {
        		Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
        	}else {
        		// 追加する
	        	Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.SET_LISTEN_RESULT, data.getJSONObject("payload").getJSONArray("users"));
	            Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
        }
    }
}


