package jp.hayamiti.state;

/**
 * 状態管理用クラス 必要に応じてステートを追加して使う
 * @author HayamitiHirotaka
 *
 */
public class Store {
    public static final String SOTA_STATE = "sota-state";
    public static final String FIND_NAME_STATE = "find-name-state";
    public static final String YES_OR_NO_STATE = "yes-or-no-state";
    public static final String SPEECH_REC_STATE = "speech_rec_state";
    private static final State sotaState = new SotaState();
    private static final State findNameState = new FindNameState();
    private static final State yesOrNoState = new YesOrNoState();
    private static final State speechRecState = new SpeechRecState();
    /**
     * ステートの取得 キャスト推奨
     * @param stateName 欲しいステートの名前
     * @return ステート
     */
    public static State getState(String stateName){
        switch (stateName){
            case SOTA_STATE:
                return sotaState;
            case FIND_NAME_STATE:
            	return findNameState;
            case YES_OR_NO_STATE:
            	return yesOrNoState;
            case SPEECH_REC_STATE:
            	return speechRecState;
            default:

                break;
        }
        return null;
    }

    /**
     * 値の変更
     * @param stateName 更新したいステートの名前
     * @param action アクション名
     * @param val 値
     * @param <T>　型
     */
    public static <T> void dispatch(String stateName, String action,T val){
        switch (stateName){
            case SOTA_STATE:
                sotaState.change(action, val);
                break;
            case FIND_NAME_STATE:
            	findNameState.change(action, val);
            	break;
            case YES_OR_NO_STATE:
            	yesOrNoState.change(action, val);
            	break;
            case SPEECH_REC_STATE:
            	speechRecState.change(action, val);
            	break;
            default:
                break;
        }
    }
}
