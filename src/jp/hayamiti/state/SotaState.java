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
    public enum Action{
	    TOGGLE_RECORD,
	    UPDATE_RECORD_RESULT,
	    UPDATE_SP_REC_RESULT,
	    UPDATE_MODE
    }
    //</action>
    //<mode>
    public enum Mode{
	    WAIT,
	    LISTENING,
	    JUDDGING,
	    RECORDING,
	    CAPTUREING,
	    FIND_NAME,
	    CONFORM_ALEADY_LISTENED,
	    LISTEN_HABIT,
	    LISTEN_CONDITION,
	    ADVISE,
	    FIN
    }
    //</mode>
    //<state>
    private boolean isRecord = false;
    private ArrayList<String> recordResult = new ArrayList<String >();
    private String spRecResult = "";
    private Enum<Mode> mode = Mode.WAIT;
    //</state>
    @Override
    public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + toString());
        // break忘れんなよ!
        switch ((Action)action){
            case TOGGLE_RECORD:
                isRecord = !isRecord;
                break;
            case UPDATE_RECORD_RESULT:
                recordResult.add((String) val);
                break;
            case UPDATE_SP_REC_RESULT:
            	spRecResult = (String) val;
            	break;
            case UPDATE_MODE:
            	mode = (Mode) val;
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
    public Enum<Mode> getMode() {
    	return mode;
    }
    //</getter>
}
