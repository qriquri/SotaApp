package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class YesOrNoState extends State{

	private static final String LOG_TAG = "YesOrNoState";
    //<action>
    public class Action{
	    public static final String UPDATE_MODE = "update-mode";
	    public static final String SET_ISYES = "set-is-yes";
    }
    //</action>
    //<mode>
    public class Mode{
	    public static final String LISTENNING_YES_OR_NO = "listening_yes_or_no";
	    public static final String WAIT = "wait";
	    public static final String LISTENED_YES_OR_NO = "listened_yes_or_no";
	    public static final String ERROR = "error";

    }
    //</mode>
    //<state>
    private String mode = Mode.LISTENNING_YES_OR_NO;
    private boolean isYes = false;
    //</state>
    @Override
    public <T> void change(String  action, T val){
    	MyLog.info(LOG_TAG, "change:" + action);
        // break忘れんなよ!
        switch (action){
            case Action.UPDATE_MODE:
            	mode = (String) val;
            	break;
            case Action.SET_ISYES:
            	isYes = (boolean) val;
            	break;
            default:
                break;
        }
    }

    //<getter>
    public String getMode() {
    	return mode;
    }
    public boolean getIsYes() {
    	return isYes;
    }
    //</getter>
}
