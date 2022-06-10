package jp.hayamiti.state;


import java.util.ArrayList;

import jp.hayamiti.utils.MyLog;

/**
 * Sotaの状態管理クラス
 * @author HayamitiHirotaka
 *
 */
public class SotaState extends State{
	private static final String LOG_TAG = "SotaState";
    //<action>
    public class Action{
	    public static final String TOGGLE_RECORD = "toggle-record";
	    public static final String UPDATE_RECORD_RESULT = "update-record-result";
	    public static final String UPDATE_SP_REC_RESULT = "update-sp-rec-result";
	    public static final String UPDATE_MODE = "update-mode";
    }
    //</action>
    //<mode>
    public class Mode{
	    public static final String WAIT = "wait";
	    public static final String LISTENING = "listening";
	    public static final String JUDDGING = "juddging";
	    public static final String RECORDING = "recording";
	    public static final String CAPTUREING = "captureing";
	    public static final String FIND_NAME = "find_name";
	    public static final String CONFORM_ALEADY_LISTENED = "conform-aleady-listened";
	    public static final String LISTEN_HABIT = "listen_habit";
	    public static final String LISTEN_CONDITION = "listen_condition";
	    public static final String ADVISE = "advise";
	    public static final String FIN = "fin";

    }
    //</mode>
    //<state>
    private boolean isRecord = false;
    private ArrayList<String> recordResult = new ArrayList<String >();
    private String spRecResult = "";
    private String mode = Mode.WAIT;
    //</state>
    @Override
    public <T> void change(String  action, T val){
    	MyLog.info(LOG_TAG, "change:" + action);
        // break忘れんなよ!
        switch (action){
            case Action.TOGGLE_RECORD:
                isRecord = !isRecord;
                break;
            case Action.UPDATE_RECORD_RESULT:
                recordResult.add((String) val);
                break;
            case Action.UPDATE_SP_REC_RESULT:
            	spRecResult = (String) val;
            	break;
            case Action.UPDATE_MODE:
            	mode = (String) val;
            	break;
            default:
                break;
        }
    }

    //<getter>
    public boolean getIsRecord(){return  isRecord; }
    public ArrayList<String> getRecordResult(){
        return recordResult;
    }
    public String getSpRecResult() {
    	return spRecResult;
    }
    public String getMode() {
    	return mode;
    }
    //</getter>
}
