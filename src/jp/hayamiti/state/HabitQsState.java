package jp.hayamiti.state;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class HabitQsState extends State {
	private static final String LOG_TAG = "HabitQs";
	public static final int IS_EXERCISE = 0;
	public static final int IS_DRINKING = 1;
	public static final int EAT_BREAKFAST = 2;
	public static final int EAT_SNACK = 3;
	public static final int SNACK_NAME = 4;
	public static final int SLEEP = 5;
	public static final int GETUP = 6;

	public class Action{
		public static final String UPDATE_MODE = "update-mode";
		public static final String RESET_RESULT = "reset-result";
		public static final String SET_QUESTION_NUM = "set-question-num";
		public static final String SET_EXERCISE_LISTEN_RESULT = "set-exercise-listen-result";
		public static final String SET_DRINGKING_LISTEN_RESULT = "set-drinking-lisetn-result";
		public static final String SET_EATBREAKFAST_LISTEN_RESULT = "set-eatBreakfast-listen-result";
		public static final String SET_EATSNACK_LISTEN_RESULT = "set-eatsnack-listen-result";
		public static final String SET_SNACKNAME_LISTEN_RESULT = "set-snackName-listen-result";
		public static final String SET_SLEEP_LISTEN_RESULT = "set-sleep-listen-result";
		public static final String SET_GETUP_LISTEN_RESULT = "set-getUp-listen-result";
	}

	public class Mode{
		public static final String LISTEN_ANS = "listen-ans";
		public static final String WAIT_SERVER_RES = "wait-server-res";
		public static final String CONFORM_ANS = "conform-ans";
		public static final String WAIT_CONFORM_ANS = "wait-conform-ans";
		public static final String ERROR_ANS = "error-ans";
	}

	private String mode = Mode.LISTEN_ANS;
	private int questionNum = IS_EXERCISE;
	private ArrayList<JSONObject> result = new ArrayList<JSONObject>(Arrays.asList(null, null, null, null, null, null,null));

	@Override
	public <T> void change(String action, T val) {
		// TODO 自動生成されたメソッド・スタブ
		MyLog.info(LOG_TAG, "change: " + action);

		switch (action) {
		case Action.UPDATE_MODE:
			mode = (String) val;
			break;
		case Action.RESET_RESULT:
			String itemStr = "{\"result\": \"\", \"text\": \"\"}";
			JSONObject item = new JSONObject(itemStr);
			result = new ArrayList<JSONObject>(Arrays.asList(item, item, item, item, item, item, item));
			break;
		case Action.SET_QUESTION_NUM:
			questionNum = (int) val;
			break;
		case Action.SET_EXERCISE_LISTEN_RESULT:
			result.set(IS_EXERCISE, (JSONObject) val);
			break;
		case Action.SET_DRINGKING_LISTEN_RESULT:
			result.set(IS_DRINKING, (JSONObject) val);
			break;
		case Action.SET_EATBREAKFAST_LISTEN_RESULT:
			result.set(EAT_BREAKFAST, (JSONObject) val);
			break;
		case Action.SET_EATSNACK_LISTEN_RESULT:
			result.set(EAT_SNACK, (JSONObject) val);
			break;
		case Action.SET_SNACKNAME_LISTEN_RESULT:
			result.set(SNACK_NAME, (JSONObject) val);
			break;
		case Action.SET_SLEEP_LISTEN_RESULT:
			result.set(SLEEP, (JSONObject) val);
			break;
		case Action.SET_GETUP_LISTEN_RESULT:
			result.set(GETUP, (JSONObject) val);
			break;
		}
	}

	public String getMode() {
		return mode;
	}
	public int getQsNum() {
		return questionNum;
	}
	public ArrayList<JSONObject> getResult(){
		return result;
	}

}
