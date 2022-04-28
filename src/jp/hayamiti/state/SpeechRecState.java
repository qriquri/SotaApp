package jp.hayamiti.state;

import jp.hayamiti.utils.MyLog;

public class SpeechRecState extends State {
	private static final String LOG_TAG = "SpeechRecState";
    //<action>
    public class Action{
	    public static final String UPDATE_MODE = "update-mode";
	    public static final String UPDATE_RESULT = "update-result";
//	    public static final String UPDATE_IS_FINISH = "set-is-finish";
    }
    //</action>
    //<mode>
    public class Mode{
    	public static final String NON_LISTENING = "non_listening";
    	public static final String PASSIVE_LISTENING = "passive_listening"; // 何かを聞き取れていないが、録音はしている状態
    	public static final String ACTIVE_LISTENING = "active_listening"; // 何かを聞き取り、話が終わるまで、録音している状態
    	public static final String FINISH_LISTENING = "finish_listening";
    }
    //</mode>
    //<state>
    private String mode = Mode.NON_LISTENING;
    private String result = "null";
    private long finishTime = 0;
    private int count = 0; // 一回の音声認識中の何回目の録音かを格納する
//    private boolean isFinish = false;
    //</state>
    @Override
    public <T> void change(String  action, T val){
    	MyLog.info(LOG_TAG, "change:" + action);
        // break忘れんなよ!
        switch (action){
            case Action.UPDATE_MODE:
            	mode = (String) val;
            	if(mode==Mode.NON_LISTENING) {
            		count = 0;
            	}else if(mode == Mode.FINISH_LISTENING) {
            		finishTime = System.currentTimeMillis();
            	}
            	break;
            case Action.UPDATE_RESULT:
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
    public String getMode() {
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
