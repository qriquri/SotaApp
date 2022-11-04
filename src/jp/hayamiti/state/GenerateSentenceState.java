package jp.hayamiti.state;

import jp.hayamiti.httpCon.ApiCom.GenerateSentenceRes;
import jp.hayamiti.utils.MyLog;

public class GenerateSentenceState extends State {

    private static final String LOG_TAG = "ConditionQsState";

    public enum Action{
        UPDATE_RESULT
    }

    private GenerateSentenceRes result = null;

    @Override
    final public <T> void dispatch(Enum<?> action, T val){

        MyLog.info(LOG_TAG, "change:" + action.toString());

        switch((Action)action) {
        case UPDATE_RESULT:
            result = (GenerateSentenceRes)val;
            break;
        }

    }

    final public GenerateSentenceRes getResult() {
        return result;
    }

}
