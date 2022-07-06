package jp.hayamiti.state;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.utils.MyLog;

public class SpRecState extends State {
	private static final String LOG_TAG = "SpeechRecState";
    //<action>
    public enum Action{
	    UPDATE_MODE,
	    UPDATE_RESULT,
	    UPDATE_ALTER,
	    SET_METHOD
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
    public enum Method{
    	GOOGLE,
    	SOTA_CLOUD,
    }
    //<state>
    private Enum<Mode> mode = Mode.NON_LISTENING;
    private String result = "";
    private List<String> alternative = new ArrayList<String>();
    private Enum<Method> method = Method.GOOGLE; // speechRecに何を使うか
//    private long finishTime = 0;
//    private int count = 0; // 一回の音声認識中の何回目の録音かを格納する
//    private boolean isFinish = false;
    //</state>
    @SuppressWarnings("unchecked")
	@Override
    public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action)action){
            case UPDATE_MODE:
            	mode = (Mode) val;
//            	if(mode==Mode.NON_LISTENING) {
//            		count = 0;
//            	}else if(mode == Mode.FINISH_LISTENING) {
//            		finishTime = System.currentTimeMillis();
//            	}
            	break;
            case UPDATE_RESULT:
            	result = (String) val;
//            	count++;
            	break;
            case UPDATE_ALTER:
            	alternative = (List<String>) val;
            	break;
            case SET_METHOD:
            	method = (Method)val;
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

    public List<String> getAlternative(){
    	return alternative;
    }
    public Enum<Method> getMethod(){
    	return method;
    }
    //</getter>

}
