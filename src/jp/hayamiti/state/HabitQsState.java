package jp.hayamiti.state;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class HabitQsState extends State {
	private static final String LOG_TAG = "HabitQsState";
	public enum QuestionI{
		IS_EXERCISE,
		IS_DRINKING,
		EAT_BREAKFAST,
		EAT_SNACK,
		SNACK_NAME,
		SLEEP,
		GETUP,
	}
	// public static final int IS_EXERCISE = 0;
	// public static final int IS_DRINKING = 1;
	// public static final int EAT_BREAKFAST = 2;
	// public static final int EAT_SNACK = 3;
	// public static final int SNACK_NAME = 4;
	// public static final int SLEEP = 5;
	// public static final int GETUP = 6;

	public enum Action{
		UPDATE_MODE,
		RESET_RESULT,
		SET_QUESTION_NUM,
		SET_QUESTION_IDX,
		SET_EXERCISE_LISTEN_RESULT,
		SET_DRINGKING_LISTEN_RESULT,
		SET_EATBREAKFAST_LISTEN_RESULT,
		SET_EATSNACK_LISTEN_RESULT,
		SET_SNACKNAME_LISTEN_RESULT,
		SET_SLEEP_LISTEN_RESULT,
		SET_GETUP_LISTEN_RESULT
	}

	public enum Mode{
		LISTEN_ANS,
		WAIT_SERVER_RES,
		CONFORM_ANS,
		WAIT_CONFORM_ANS,
		ERROR_ANS
	}

	private Enum<Mode> mode = Mode.LISTEN_ANS;
	// private int questionNum = IS_EXERCISE;
	private Enum<QuestionI> questionI = QuestionI.IS_EXERCISE;
	private ArrayList<JSONObject> result = new ArrayList<JSONObject>(Arrays.asList(null, null, null, null, null, null,null));

	@Override
	public <T> void dispatch(Enum<?> action, T val){

		MyLog.info(LOG_TAG, "change: " + action.toString());

		switch ((Action)action) {
		case UPDATE_MODE:
			mode = (Mode) val;
			break;
		case RESET_RESULT:
			String itemStr = "{\"result\": \"\", \"text\": \"\"}";
			JSONObject item = new JSONObject(itemStr);
			result = new ArrayList<JSONObject>(Arrays.asList(item, item, item, item, item, item, item));
			break;
		// case SET_QUESTION_NUM:
		// 	questionNum = (int) val;
		// 	break;
		case SET_QUESTION_IDX:
			questionI = (QuestionI) val;
			break;
		case SET_EXERCISE_LISTEN_RESULT:
			result.set(QuestionI.IS_EXERCISE.ordinal(), (JSONObject) val);
			break;
		case SET_DRINGKING_LISTEN_RESULT:
			result.set(QuestionI.IS_DRINKING.ordinal(), (JSONObject) val);
			break;
		case SET_EATBREAKFAST_LISTEN_RESULT:
			result.set(QuestionI.EAT_BREAKFAST.ordinal(), (JSONObject) val);
			break;
		case SET_EATSNACK_LISTEN_RESULT:
			result.set(QuestionI.EAT_SNACK.ordinal(), (JSONObject) val);
			break;
		case SET_SNACKNAME_LISTEN_RESULT:
			result.set(QuestionI.SNACK_NAME.ordinal(), (JSONObject) val);
			break;
		case SET_SLEEP_LISTEN_RESULT:
			result.set(QuestionI.SLEEP.ordinal(), (JSONObject) val);
			break;
		case SET_GETUP_LISTEN_RESULT:
			result.set(QuestionI.GETUP.ordinal(), (JSONObject) val);
			break;
		default:
			break;
		}
	}

	public Enum<Mode> getMode() {
		return mode;
	}
	// public int getQsNum() {
	// 	return questionNum;
	// }

	public Enum<QuestionI> getQuestionI(){
		return questionI;
	}
	public ArrayList<JSONObject> getResult(){
		return result;
	}

}
