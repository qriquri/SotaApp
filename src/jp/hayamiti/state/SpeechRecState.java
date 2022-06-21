package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class SpeechRecState extends State {
	private static final String LOG_TAG = "SpeechRecState";
    //<action>
    public enum Action{
	    UPDATE_MODE,
	    UPDATE_RESULT
//	    UPDATE_IS_FINISH = "set-is-finish";
    }
    //</action>
    //<mode>
    public enum Mode{
    	NON_LISTENING,
    	PASSIVE_LISTENING, // 何かを聞き取れていないが、録音はしている状態
    	ACTIVE_LISTENING, // 何かを聞き取り、話が終わるまで、録音している状態
    	FINISH_LISTENING
    }
    //</mode>
    //<state>
    private Enum<Mode> mode = Mode.NON_LISTENING;
    private String result = "null";
    private long finishTime = 0;
    private int count = 0; // 一回の音声認識中の何回目の録音かを格納する
//    private boolean isFinish = false;
    //</state>
    @Override
    public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action)action){
            case UPDATE_MODE:
            	mode = (Mode) val;
            	if(mode==Mode.NON_LISTENING) {
            		count = 0;
            	}else if(mode == Mode.FINISH_LISTENING) {
            		finishTime = System.currentTimeMillis();
            	}
            	break;
            case UPDATE_RESULT:
            	result = (String) val;
            	count++;
            	break;
//            case Action.UPDATE_IS_FINISH:
//            	isFinish = (boolean) val;
            default:
                break;
        }
    }

    //<getter>
    public Enum<Mode> getMode() {
    	return mode;
    }
    public String getResult(){
    	return result;
    }
    public int getCount() {
    	return count;
    }
    public long getFinishTime() {
    	return finishTime;
    }
//    public boolean getIsFinish() {
//    	return isFinish;
//    }
    //</getter>

}
