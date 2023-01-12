package jp.hayamiti.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jp.hayamiti.utils.MyLog;

/**
 * 状態管理用クラス 必要に応じてステートを追加して使う
 * @author HayamitiHirotaka
 *
 */
final public class Store {
	private static final String TAG = "Store";
    private static Map<Class<? extends State>, State> stateMap = new HashMap<>();
	/**
	 * ステートを束ねる
	 * @param stateList
	 */
	final public static void bind(ArrayList<State> stateList) {
    	for (int i = 0; i < stateList.size(); i++) {
    		stateMap.put(stateList.get(i).getClass(), stateList.get(i));
    	}
    }

	final public static void reset() {
	    stateMap = new HashMap<>();
	}

	/**
	 * 指定されたステートを指定されたアクションに応じて処理する
	 * @param <T>
	 * @param klass
	 * @param action
	 * @param val
	 */
	final public static  <T>  void dispatch(Class<? extends State> klass, Enum<?> action, T val) {
		State state = getState(klass);
		state.dispatch(action, val);
	}

	/**
     * ステートの取得 キャスト推奨
     * @param klass ほしいステートのクラス
     * @return ステート
     */
    final public static State getState(Class<? extends State> klass){
    	// null だったら例外を出す
    	Optional<State> stateOpt = Optional.of(stateMap.get(klass));
        return stateOpt.get();
    }

	final public static void main(String[] args) {
		ArrayList<State> stateList = new ArrayList<State>(){{
			add(new FindNameState());
		}};
		Store.bind(stateList);

		FindNameState fnState = (FindNameState) Store.getState(FindNameState.class);
		fnState.dispatch(FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_CONFORM);
		MyLog.info(TAG, fnState.getMode().toString());

		Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
		MyLog.info(TAG, fnState.getMode().toString());
	}
}
