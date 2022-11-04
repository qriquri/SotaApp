package jp.hayamiti.state;

import java.util.ArrayList;

public class StoreTest4 {
    public static final void setup() {
      //Store 初期化 stateを束ねる
        ArrayList<State> stateList = new ArrayList<State>() {
            {
                add(new SotaState());
                add(new SpRecState());
                add(new TextToSpeechState());
                add(new FindNameState());
                add(new YesOrNoState());
                add(new HabitQsState());
                add(new ConditionQsState());
                add(new DayQsState());
                add(new SuggestNextHabitState());
                add(new GenerateSentenceState());
            }
        };
        Store.bind(stateList);
    }

    public static final SotaState getSotaState() {
        return (SotaState) Store.getState(SotaState.class);
    }

    public static final SpRecState getSpRecState() {
        return  (SpRecState) Store.getState(SpRecState.class);
    }

    public static final TextToSpeechState getTextToSpeechState() {
        return  (TextToSpeechState) Store.getState(TextToSpeechState.class);
    }

    public static final FindNameState getFindNameState() {
        return (FindNameState) Store.getState(FindNameState.class);
    }

    public static final DayQsState getDayQsState() {
        return (DayQsState) Store.getState(DayQsState.class);
    }
}
