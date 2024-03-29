package jp.hayamiti.state;

import jp.hayamiti.httpCon.ApiCom.ConditionQsRes;
import jp.hayamiti.utils.MyLog;

final public class ConditionQsState extends State {
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
	private ConditionQsRes result = null;

	@Override
	final public <T> void dispatch(Enum<?> action, T val){

		MyLog.info(LOG_TAG, "change:" + action.toString());

		switch((Action)action) {
		case UPDATE_MODE:
			mode = (Mode)val;
			break;
		case RESET_RESULT:
//			String itemStr = "{\"result\": \"\", \"text\": \"\"}";
			result.setResult("");
			result.setText("");
			break;
		case SET_LISTEN_RESULT:
			result = (ConditionQsRes)val;
			break;
		}

	}

	final public Enum<Mode> getMode() {
		return mode;
	}
	final public ConditionQsRes getResult() {
		return result;
	}

}
