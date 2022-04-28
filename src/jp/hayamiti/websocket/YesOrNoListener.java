package jp.hayamiti.websocket;

import org.json.JSONObject;

import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.hayamiti.utils.MyLog;

public class YesOrNoListener extends Listener{

	public static final String CHANNEL = "YES_OR_NO";
    private final String LOG_TAG = "YesOrNoListener";
    public YesOrNoListener(){
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
        	MyLog.info(LOG_TAG, data.getString("payload"));
        	if(data.getString("payload").equals("yes")) {
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.SET_ISYES, true);
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else if(data.getString("payload").equals("no")){
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.SET_ISYES, false);
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else {
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.ERROR);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
