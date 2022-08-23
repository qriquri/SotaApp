package jp.hayamiti.state;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.utils.MyLog;

final public class SpRecState extends State {
	private static final String LOG_TAG = "SpeechRecState";
    //<action>
    public enum Action{
	    UPDATE_RESULT,
	    UPDATE_ALTER,
	    SET_METHOD
    }
    //</action>
    //<mode>
    //</mode>
    public enum Method{
    	GOOGLE,
    	SOTA_CLOUD,
    }
    //<state>
    private String result = "";
    private List<String> alternative = new ArrayList<String>();
    private Enum<Method> method = Method.GOOGLE; // speechRecに何を使うか
    //</state>
    @SuppressWarnings("unchecked")
	@Override
    final public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action)action){
            case UPDATE_RESULT:
            	result = (String) val;
            	break;
            case UPDATE_ALTER:
            	alternative = (List<String>) val;
            	break;
            case SET_METHOD:
            	method = (Method)val;
            	break;
            default:
                break;
        }
    }

    //<getter>
    final public String getResult(){
    	return result;
    }

    final public List<String> getAlternative(){
    	return alternative;
    }
    final public Enum<Method> getMethod(){
    	return method;
    }
    //</getter>

}
