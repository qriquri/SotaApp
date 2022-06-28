package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class DayQsState extends State {
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
	public <T> void dispatch(Enum<?> action, T val) {
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
	public Enum<Mode> getMode() {
		return mode;
	}

	public int getResult() {
		return result;
	}


}
