package jp.hayamiti.state;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class FindNameState extends State {
	private static final String LOG_TAG = "FindNameState";
    //<action>
    public class Action{
	    public static final String UPDATE_MODE = "update-mode";
	    public static final String ADD_NAME = "add-name";
	    public static final String SET_LISTEN_RESULT = "set_listen_result";
	    public static final String REMOVE_NAME = "remove-name";
	    public static final String SET_ISCOLLECT = "set-is-collect";
	    public static final String COUNT = "count";
    }
    //</action>
    //<mode>
    public class Mode{
	    public static final String LISTENNING_NAME = "listening_name";
	    public static final String WAIT_FIND_NAME = "wait_find_name";
	    public static final String CONFORM_NAME = "conform_name";
	    public static final String WAIT_CONFORM = "wait_conform"; // yes/no応答待ち
	    public static final String WAIT_CONFORM_MULTIPLE = "wait_confrom_multiple"; // 同じ名前が複数ある場合
	    public static final String FINDED_NAME = "finded_name";
	    public static final String ERROR_NAME = "error_name";

    }
    //</mode>
    //<state>
    private String mode = Mode.LISTENNING_NAME;
    private ArrayList<JSONObject> results = new ArrayList<JSONObject>(); // 確認済みの正しい結果
    private JSONArray listenResults = new JSONArray(); // 聞き取っただけのまだ確認していない結果
    private int count = 0; // 名前確認の際に何番目の名前をチェックしているか
    private boolean isCollect = false;
    //</state>
    @Override
    public <T> void change(String  action, T val){
    	MyLog.info(LOG_TAG, "change:" + action);
        // break忘れんなよ!
        switch (action){
            case Action.UPDATE_MODE:
            	mode = (String) val;
            	break;
            case Action.ADD_NAME:
            	results.add((JSONObject) val);
            	break;
            case Action.REMOVE_NAME:
            	results.remove((int) val);
            	break;
            case Action.SET_ISCOLLECT:
            	isCollect = (boolean) val;
            	break;
            case Action.SET_LISTEN_RESULT:
            	listenResults = (JSONArray)val;
            	count=0;
            	break;
            case Action.COUNT:
            	count++;
            default:
                break;
        }
    }

    //<getter>
    public String getMode() {
    	return mode;
    }
    public ArrayList<JSONObject> getResults(){
    	return results;
    }
    public boolean getIsCollect() {
    	return isCollect;
    }
    public JSONArray getListenResults() {
    	return listenResults;
    }
    public int getCount() {
    	return count;
    }
    //</getter>

}
