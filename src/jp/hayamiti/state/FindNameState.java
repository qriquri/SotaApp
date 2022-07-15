package jp.hayamiti.state;

import java.util.ArrayList;

import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.utils.MyLog;

final public class FindNameState extends State {
	private static final String LOG_TAG = "FindNameState";
    //<action>
    public enum Action{
	    UPDATE_MODE,
	    ADD_NAME,
	    SET_LISTEN_RESULT,
	    REMOVE_NAME,
	    SET_ISCOLLECT,
	    COUNT
    }
    //</action>
    //<mode>
    public enum Mode{
	    LISTENNING_NAME,
	    WAIT_FIND_NAME,
	    CONFORM_NAME,
	    WAIT_CONFORM,// yes/no応答待ち
	    WAIT_CONFORM_MULTIPLE,// 同じ名前が複数ある場合
	    FINDED_NAME,
	    ERROR_NAME,

    }
    //</mode>
    //<state>
    private Enum<Mode> mode = Mode.LISTENNING_NAME;
    private ArrayList<User> results = new ArrayList<User>(); // 確認済みの正しい結果
    private ArrayList<User> listenResults = new ArrayList<User>(); // 聞き取っただけのまだ確認していない結果
    private int count = 0; // 名前確認の際に何番目の名前をチェックしているか
    private boolean isCollect = false;
    //</state>
    @SuppressWarnings("unchecked")
	@Override
    final public <T> void dispatch(Enum<?> action, T val){
    	MyLog.info(LOG_TAG, "change:" + action.toString());
        // break忘れんなよ!
        switch ((Action) action){
            case UPDATE_MODE:
            	mode = (Mode) val;
            	break;
            case ADD_NAME:
            	results.add((User) val);
            	break;
            case REMOVE_NAME:
            	results.remove((int) val);
            	break;
            case SET_ISCOLLECT:
            	isCollect = (boolean) val;
            	break;
            case SET_LISTEN_RESULT:
            	listenResults = (ArrayList<User>)val;
//            	MyLog.info(LOG_TAG, listenResults.get(0).furigana);
            	count=0;
            	break;
            case COUNT:
            	count++;
            default:
                break;
        }
    }

    //<getter>
    final public Enum<Mode> getMode() {
    	return mode;
    }
    final public ArrayList<User> getResults(){
    	return results;
    }
    final public boolean getIsCollect() {
    	return isCollect;
    }
    final public ArrayList<User> getListenResults() {
    	return listenResults;
    }
    final public int getCount() {
    	return count;
    }
    //</getter>

}
