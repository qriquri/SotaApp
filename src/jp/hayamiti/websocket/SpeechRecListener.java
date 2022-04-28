package jp.hayamiti.websocket;

import org.json.JSONObject;

import jp.hayamiti.state.SpeechRecState;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;

public class SpeechRecListener extends Listener {
	public static final String CHANNEL = "SPEECH_REC";
    private final String LOG_TAG = "SpeechRecListener";
    public SpeechRecListener(){
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
        	String mode = ((SpeechRecState)Store.getState(Store.SPEECH_REC_STATE)).getMode();
        	long finishTime = ((SpeechRecState)Store.getState(Store.SPEECH_REC_STATE)).getFinishTime();
        	MyLog.info(LOG_TAG, "finishTime:"+finishTime);
        	JSONObject payload = data.getJSONObject("payload");
        	if(finishTime > payload.getLong("sendTime")) {
        		MyLog.info(LOG_TAG, "過去のデータ!");
        		return;
        	}
        	if(mode == SpeechRecState.Mode.PASSIVE_LISTENING) {
        		if(!payload.getString("result").equals("")) {
        			// 文字が含まれていたら
        			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.FINISH_LISTENING);
            		// 結果を登録
            		Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_RESULT, payload.getString("result"));
        		}
        	}else if (mode == SpeechRecState.Mode.ACTIVE_LISTENING) {
        		String prevResult = ((SpeechRecState)Store.getState(Store.SPEECH_REC_STATE)).getResult();
        		if(prevResult.equals(payload.getString("result"))){
        			// 送られてきた結果が一つ前の結果と同じとき
//        			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_IS_FINISH, true);
        			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.FINISH_LISTENING);
        		}else {
        			// 結果を登録
        			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_RESULT, payload.getString("result"));

        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
