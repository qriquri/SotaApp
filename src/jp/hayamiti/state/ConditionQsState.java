package jp.hayamiti.state;

import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class ConditionQsState extends State {
	private static final String LOG_TAG = "ConditionQsState";

	public class Action{
		public static final String UPDATE_MODE = "update-mode";
		public static final String RESET_RESULT = "reset-result";
		public static final String SET_LISTEN_RESULT = "set-listen-result";
	}

	public class Mode{
		public static final String LISTEN_ANS = "listen-ans";
		public static final String CONFORM_ANS = "conform-ans";
		public static final String WAIT_CONFORM_ANS = "wait-conform-ans";
		public static final String ERROR_ANS = "error-ans";
	}

	private String mode = Mode.LISTEN_ANS;
	private JSONObject result = null;

	@Override
	public <T> void change(String action, T val) {
		// TODO 自動生成されたメソッド・スタブ
		MyLog.info(LOG_TAG, "change:" + action);

		switch(action) {
		case Action.UPDATE_MODE:
			mode = (String)val;
			break;
		case Action.RESET_RESULT:
			String itemStr = "{\"result\": \"\", \"text\": \"\"}";
			result = new JSONObject(itemStr);
			break;
		case Action.SET_LISTEN_RESULT:
			result = (JSONObject)val;
			break;
		}

	}

	public String getMode() {
		return mode;
	}
	public JSONObject getResult() {
		return result;
	}

}
