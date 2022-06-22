package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class YesOrNoState extends State{

	private static final String LOG_TAG = "YesOrNoState";
    //<action>
    public enum Action{
	    UPDATE_MODE,
	    SET_ISYES
    }
    //</action>
    //<mode>
    public enum Mode{
	    LISTENNING_YES_OR_NO,
	    WAIT,
	    LISTENED_YES_OR_NO,
	    ERROR

    }
    //</mode>
    //<state>
    private Enum<Mode> mode = Mode.LISTENNING_YES_OR_NO;
    private boolean isYes = false;
    //</state>
    @Override
    public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action) action){
            case UPDATE_MODE:
            	mode = (Mode) val;
            	break;
            case SET_ISYES:
            	isYes = (boolean) val;
            	break;
            default:
                break;
        }
    }

    //<getter>
    public Enum<Mode> getMode() {
    	return mode;
    }
    public boolean getIsYes() {
    	return isYes;
    }
    //</getter>
}
