package jp.hayamiti;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;

class HabitQsTest {
    void setupStore() {
        Store.reset();
        ArrayList<State> stateList = new ArrayList<State>() {
            {
                add(new HabitQsState());

            }
        };
        Store.bind(stateList);
    }
    @Nested
    class yesNO質問の答えを反転させる{
        @Test
        void _すでに質問済みならtrueを返す() {
            setupStore();
            // 実行
            HabitQsRes res = new HabitQsRes();
            res.setResult("yes");
            res.setSendTime(0);
            res.setText("運動した");
            Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT, res);
            Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_QUESTION_IDX, HabitQsState.QuestionI.IS_DRINKING);
            Method method;
            boolean result = false;
            try {
                method = HabitQs.class.getDeclaredMethod("isAlreadyListened");
                method.setAccessible(true);
                result = (boolean)method.invoke(null);
            } catch (Exception e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
                fail();
            }
//          System.out.println(result);
            // 検証 前が期待値
            assertEquals(true, result);
        }
        @Test
        void _運動したなら運動してない() {
            setupStore();

            // 実行
            HabitQsRes res = new HabitQsRes();
            res.setResult("yes");
            res.setSendTime(0);
            res.setText("運動した");
            Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT, res);

            Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_QUESTION_IDX, HabitQsState.QuestionI.IS_DRINKING);
            final HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
            Method method;
            boolean result = true;
            assertEquals(result, state.getResult().isExercise());

            try {
                method = HabitQs.class.getDeclaredMethod("fixQs");
                method.setAccessible(true);
                method.invoke(null);
                result = state.getResult().isExercise();
            } catch (Exception e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
                fail();
            }
//          System.out.println(result);
            // 検証 前が期待値
            assertEquals(false, result);
        }
    }

}
