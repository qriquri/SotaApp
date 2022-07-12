package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class TextToSpeechState extends State {
	private final String LOG_TAG = "TextToSpeechState";
	public enum Action{
		SET_METHOD
	};
	public enum Method{
		SOTA_CLOUD,
		OPEN_J_TALK

	}
	private Enum<Method> method = Method.OPEN_J_TALK;
	@Override
	public <T> void dispatch(Enum<?> action, T val) {
		MyLog.info(LOG_TAG, "change:" + action.toString());

		switch((Action) action) {
		case SET_METHOD:
			method = (Method) val;
			break;
		default:
			break;
		}

	}

	public Enum<Method> getMethod(){
		return method;
	}

}
