package jp.hayamiti.state;

import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class ConditionQsState extends State {
	private static final String LOG_TAG = "ConditionQsState";

	public enum Action{
		UPDATE_MODE,
		RESET_RESULT,
		SET_LISTEN_RESULT
	}

	public enum Mode{
		LISTEN_ANS,
		CONFORM_ANS,
		WAIT_CONFORM_ANS,
		ERROR_ANS
	}

	private Enum<Mode> mode = Mode.LISTEN_ANS;
	private JSONObject result = null;

	@Override
	public <T> void dispatch(Enum<?> action, T val){
		
		MyLog.info(LOG_TAG, "change:" + action.toString());

		switch((Action)action) {
		case UPDATE_MODE:
			mode = (Mode)val;
			break;
		case RESET_RESULT:
			String itemStr = "{\"result\": \"\", \"text\": \"\"}";
			result = new JSONObject(itemStr);
			break;
		case SET_LISTEN_RESULT:
			result = (JSONObject)val;
			break;
		}

	}

	public Enum<Mode> getMode() {
		return mode;
	}
	public JSONObject getResult() {
		return result;
	}

}
