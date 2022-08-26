package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class SuggestNextHabitState extends State {
	private static final String LOG_TAG = "SuggestNextHabitState";
    //<action>
    public enum Action{
	    UPDATE_MODE,
	    SET_RESULT
    }
    //</action>
    //<mode>
    public enum Mode{
    	SUGGEST
    }
    //</mode>
    //<state>
    private Enum<Mode> mode = Mode.SUGGEST;
    private int[] result = new int[5];
    //</state>
    @Override
    final public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action) action){
            case UPDATE_MODE:
            	mode = (Mode) val;
            	break;
            case SET_RESULT:
            	result = (int[]) val;
            	break;
            default:
                break;
        }
    }

    //<getter>
    final public Enum<Mode> getMode() {
    	return mode;
    }
    final public int[] getResult() {
    	return result;
    }
    //</getter>
}
