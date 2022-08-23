package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

final public class DayQsState extends State {
	private static final String LOG_TAG = "DayQsState";
	public enum Action{
		UPDATE_MODE,
		SET_RESULT,
	}
	public enum Mode{
		LISTEN_ANS,
		CONFORM_ANS,
		WAIT_CONFORM,
	}
	private Enum<Mode> mode = Mode.LISTEN_ANS;
	private int result = 0; // 遡る日にち
	@Override
	final public <T> void dispatch(Enum<?> action, T val) {
		MyLog.info(LOG_TAG, "change:" + action.toString());

		switch((Action) action) {
		case UPDATE_MODE:
			mode = (Mode) val;
			break;
		case SET_RESULT:
			result = (int) val;
		default:
			break;
		}

	}
	final public Enum<Mode> getMode() {
		return mode;
	}

	final public int getResult() {
		return result;
	}


}
