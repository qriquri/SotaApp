package jp.hayamiti.state;

import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.httpCon.DbCom.PostHabitReq;
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
//	private ArrayList<HabitQsRes> result = new ArrayList<HabitQsRes>(Arrays.asList(null, null, null, null, null, null,null));
	private PostHabitReq result = new PostHabitReq();
	private String conformText = ""; // Sotaが答えがあってるか確認するセリフに使う

	@Override
	public <T> void dispatch(Enum<?> action, T val){

		MyLog.info(LOG_TAG, "change: " + action.toString());

		switch ((Action)action) {
		case UPDATE_MODE:
			mode = (Mode) val;
			break;
		case RESET_RESULT:
//			HabitQsRes item = new HabitQsRes();
//			result = new ArrayList<HabitQsRes>(Arrays.asList(item, item, item, item, item, item, item));
			result = new PostHabitReq();
			conformText = "";
			break;
		// case SET_QUESTION_NUM:
		// 	questionNum = (int) val;
		// 	break;
		case SET_QUESTION_IDX:
			questionI = (QuestionI) val;
			break;
		case SET_EXERCISE_LISTEN_RESULT:
//			result.set(QuestionI.IS_EXERCISE.ordinal(), (HabitQsRes) val);
			result.exercise = ((HabitQsRes) val).result.equals("yes");
			result.exerciseT = ((HabitQsRes) val).text;
			conformText = result.exercise ? "運動した" : "運動してない";
			break;
		case SET_DRINGKING_LISTEN_RESULT:
//			result.set(QuestionI.IS_DRINKING.ordinal(), (HabitQsRes) val);
			result.drinking = ((HabitQsRes) val).result.equals("yes");
			result.drinkingT = ((HabitQsRes) val).text;
			conformText = result.drinking ? "飲酒した" : "飲酒してない";
			break;
		case SET_EATBREAKFAST_LISTEN_RESULT:
//			result.set(QuestionI.EAT_BREAKFAST.ordinal(), (HabitQsRes) val);
			result.eatBreakfast = ((HabitQsRes) val).result.equals("yes");
			result.eatBreakfastT = ((HabitQsRes) val).text;
			conformText = result.eatBreakfast ? "食べた" : "食べてない";
			break;
		case SET_EATSNACK_LISTEN_RESULT:
//			result.set(QuestionI.EAT_SNACK.ordinal(), (HabitQsRes) val);
			result.eatSnack = ((HabitQsRes) val).result.equals("yes");
			result.eatSnackT = ((HabitQsRes) val).text;
			conformText = result.eatSnack ? "食べた" : "食べてない";
			break;
		case SET_SNACKNAME_LISTEN_RESULT:
//			result.set(QuestionI.SNACK_NAME.ordinal(), (HabitQsRes) val);
			result.snackName = ((HabitQsRes) val).result;
			result.snackNameT = ((HabitQsRes) val).text;
			conformText = result.snackName;
			break;
		case SET_SLEEP_LISTEN_RESULT:
//			result.set(QuestionI.SLEEP.ordinal(), (HabitQsRes) val);
			result.sleep = Integer.parseInt(((HabitQsRes) val).result);
			result.sleepT = ((HabitQsRes) val).text;
			conformText = result.sleep + "時";
			break;
		case SET_GETUP_LISTEN_RESULT:
//			result.set(QuestionI.GETUP.ordinal(), (HabitQsRes) val);
			result.getUp = Integer.parseInt(((HabitQsRes) val).result);
			result.getUpT = ((HabitQsRes) val).text;
			conformText = result.getUp + "時";
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
	public PostHabitReq getResult(){
		return result;
	}
	public String getConformText() {
		return conformText;
	}

}
