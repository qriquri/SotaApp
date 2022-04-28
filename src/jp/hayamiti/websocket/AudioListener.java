package jp.hayamiti.websocket;

import org.json.JSONException;
import org.json.JSONObject;

import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;

public class AudioListener extends Listener {
	public static final String CHANNEL = "AUDIO";
	final static String LOG_TAG = "AudioListener";
    public AudioListener(){
            super();
            setChannel(CHANNEL);
    }
    @Override
    /**
     * 録音した音声の文字起こしの結果を受け取ったら、ストアに登録し状態をJUDDGINGに変化させる
     */
    protected void callback(JSONObject data){
        try {
            MyLog.info(LOG_TAG,"get audio:" + data.getString("payload"));
            Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_SP_REC_RESULT, data.getString("payload"));
            Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
