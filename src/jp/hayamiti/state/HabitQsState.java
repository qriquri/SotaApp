package jp.hayamiti.state;

import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.httpCon.DbCom.PostHabitReq;
import jp.hayamiti.utils.MyLog;

final public class HabitQsState extends State {
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
	// final public static final int IS_EXERCISE = 0;
	// final public static final int IS_DRINKING = 1;
	// final public static final int EAT_BREAKFAST = 2;
	// final public static final int EAT_SNACK = 3;
	// final public static final int SNACK_NAME = 4;
	// final public static final int SLEEP = 5;
	// final public static final int GETUP = 6;

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
	final public <T> void dispatch(Enum<?> action, T val){

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
			result.setExercise(((HabitQsRes) val).getResult().equals("yes"));
			result.setExerciseT(((HabitQsRes) val).getText());
			conformText = result.isExercise() ? "運動した" : "運動してない";
			break;
		case SET_DRINGKING_LISTEN_RESULT:
//			result.set(QuestionI.IS_DRINKING.ordinal(), (HabitQsRes) val);
			result.setDrinking(((HabitQsRes) val).getResult().equals("yes"));
			result.setDrinkingT(((HabitQsRes) val).getText());
			conformText = result.isDrinking() ? "飲酒した" : "飲酒してない";
			break;
		case SET_EATBREAKFAST_LISTEN_RESULT:
//			result.set(QuestionI.EAT_BREAKFAST.ordinal(), (HabitQsRes) val);
			result.setEatBreakfast(((HabitQsRes) val).getResult().equals("yes"));
			result.setEatBreakfastT(((HabitQsRes) val).getText());
			conformText = result.isEatBreakfast() ? "食べた" : "食べてない";
			break;
		case SET_EATSNACK_LISTEN_RESULT:
//			result.set(QuestionI.EAT_SNACK.ordinal(), (HabitQsRes) val);
			result.setEatSnack(((HabitQsRes) val).getResult().equals("yes"));
			result.setEatSnackT(((HabitQsRes) val).getText());
			conformText = result.isEatSnack() ? "食べた" : "食べてない";
			break;
		case SET_SNACKNAME_LISTEN_RESULT:
//			result.set(QuestionI.SNACK_NAME.ordinal(), (HabitQsRes) val);
			result.setSnackName(((HabitQsRes) val).getResult());
			result.setSnackNameT(((HabitQsRes) val).getText());
			conformText = result.getSnackName();
			break;
		case SET_SLEEP_LISTEN_RESULT:
//			result.set(QuestionI.SLEEP.ordinal(), (HabitQsRes) val);
			result.setSleep(Integer.parseInt(((HabitQsRes) val).getResult()));
			result.setSleepT(((HabitQsRes) val).getText());
			conformText = result.getSleep() + "時";
			break;
		case SET_GETUP_LISTEN_RESULT:
//			result.set(QuestionI.GETUP.ordinal(), (HabitQsRes) val);
			result.setGetUp(Integer.parseInt(((HabitQsRes) val).getResult()));
			result.setGetUpT(((HabitQsRes) val).getText());
			conformText = result.getGetUp() + "時";
			break;
		default:
			break;
		}
	}

	final public Enum<Mode> getMode() {
		return mode;
	}
	// final public int getQsNum() {
	// 	return questionNum;
	// }

	final public Enum<QuestionI> getQuestionI(){
		return questionI;
	}
	final public PostHabitReq getResult(){
		return result;
	}
	final public String getConformText() {
		return conformText;
	}

}
