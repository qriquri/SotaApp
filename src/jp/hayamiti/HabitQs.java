package jp.hayamiti;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.httpCon.DbCom.PostHabitReq;
import jp.hayamiti.httpCon.DbCom.PostHabitRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.utils.MyStrBuilder;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

final public class HabitQs {
    private static final String TAG = "HabitQs";

    final public static void main(String[] args) {
        MyLog.info(TAG, "test");
        ArrayList<State> stateList = new ArrayList<State>() {
            {
                add(new HabitQsState());
            }
        };
        Store.bind(stateList);
        // int count = HabitQsState.IS_EXERCISE;
        HabitQsRes yesNoAns = new HabitQsRes();
        yesNoAns.setResult("no");
        yesNoAns.setText("してない");
        HabitQsRes timeAns = new HabitQsRes();
        timeAns.setResult("7");
        timeAns.setText("7時");
        HabitQsRes textAns = new HabitQsRes();
        textAns.setResult("チョコレート ポテトチップス");
        textAns.setText("チョコレートと ポテトチップス食べた");
        Enum<HabitQsState.QuestionI>[] questionI = HabitQsState.QuestionI.values();
        for (Enum<HabitQsState.QuestionI> i : questionI) {
            switch ((HabitQsState.QuestionI) i) {
                case IS_EXERCISE:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT, yesNoAns);
                    break;
                case IS_DRINKING:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT, yesNoAns);
                    break;
                case EAT_BREAKFAST:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT, yesNoAns);
                    break;
                case EAT_SNACK:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT, yesNoAns);
                    break;
                case SNACK_NAME:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT, textAns);
                    break;
                case SLEEP:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_SLEEP_LISTEN_RESULT, timeAns);
                    break;
                case GETUP:
                    Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_GETUP_LISTEN_RESULT, timeAns);
                    break;
            }
        }
        HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
        PostHabitReq result = state.getResult();
        try {
            MyLog.info(TAG, "getResult: " + JSONMapper.mapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        MyLog.info(TAG, "end");
    }

    final public static boolean habitQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish,
            CRecordMic mic, int backDay) {
        boolean isFinish = false;
        HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
        Enum<HabitQsState.Mode> mode = state.getMode();
        if (mode == HabitQsState.Mode.LISTEN_ANS) {
            // <質問をしてこたえを聞き取る>
            recordARec(mic, sotawish, backDay, motion);
            // </質問をしてこたえを聞き取る>
        } else if (mode == HabitQsState.Mode.CONFORM_ANS) {
            TextToSpeech.speech(MyStrBuilder.build(64, state.getConformText(), "っと"), sotawish,
                    MotionAsSotaWish.MOTION_TYPE_LOW);
            // モード更新
            Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.WAIT_CONFORM_ANS);

        } else if (mode == HabitQsState.Mode.WAIT_CONFORM_ANS) {
            // <確認待機>
            isFinish = nextQs(pose, mem, motion, sotawish, mic, backDay);
            // </確認待機>
        }

        return isFinish;
    }

    final private static void recordARec(CRecordMic mic, MotionAsSotaWish sotawish,
            int backDay, CSotaMotion motion) {
        String type = "";
        HabitQsState.Action action = null;
        String question = "";
        final HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);

        try {
            String relativeYesterday = backDay == 0 ? "昨日" : MyStrBuilder.build(12, (backDay + 1), "日前");
            String relativeToday = backDay == 0 ? "今日" : MyStrBuilder.build(12, backDay, "日前");
            // <今聞こうとしている質問に合わせた値を代入する>
            switch ((HabitQsState.QuestionI) state.getQuestionI()) {
                case IS_EXERCISE:
                    type = "exercise";
                    action = HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT;
                    question = MyStrBuilder.build(64, relativeYesterday, "運動した?");
                    break;
                case IS_DRINKING:
                    type = "drinking";
                    action = HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT;
                    question = MyStrBuilder.build(64, relativeYesterday, "お酒飲んだ?");
                    break;
                case EAT_BREAKFAST:
                    type = "eatBreakfast";
                    question = MyStrBuilder.build(64, relativeYesterday, "朝ごはん食べた?");
                    action = HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT;
                    break;
                case EAT_SNACK:
                    type = "eatSnack";
                    question = MyStrBuilder.build(64, relativeYesterday, "おやつ食べた?");
                    action = HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT;
                    break;
                case SNACK_NAME:
                    type = "snackName";
                    question = MyStrBuilder.build(64, relativeYesterday, "おやつに何食べたの?");
                    action = HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT;
                    break;
                case SLEEP:
                    type = "sleep";
                    question = MyStrBuilder.build(64, relativeYesterday,
                            "何時に寝た？例えば午後8時に寝たなら20時に寝た、夜の1時に寝たなら25時に寝たと答えてね。");
                    action = HabitQsState.Action.SET_SLEEP_LISTEN_RESULT;
                    break;
                case GETUP:
                    type = "getUp";
                    question = MyStrBuilder.build(64, relativeToday, "何時に起きた?答え方はさっきと同じでお願い。");
                    action = HabitQsState.Action.SET_GETUP_LISTEN_RESULT;
                    break;
            }
            // </今聞こうとしている質問に合わせた値を代入する>

            // 質問する
            TextToSpeech.speech(question, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
            SpeechRec.speechRec(mic, motion);
            // apiサーバーに送信して、解析してもらう
            HabitQsRes res = MyHttpCon.habitQs(((SpRecState) Store.getState(SpRecState.class)).getResult(), type);;
            CRobotUtil.Log(TAG, JSONMapper.mapper.writeValueAsString(res));
            String ans = res.getResult();

            if (ans.equals("error")) {
                TextToSpeech.speech("エラーが起きたからもう一度聞くね", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
                Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
            }else if(ans.equals("miss")) {
                backQs();
            }
            else {
                Store.dispatch(HabitQsState.class, action, res);
                // 答えがあってるか確認するモードへ
                Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);

            }
        } catch (Exception e) {
            CRobotUtil.Log(TAG, e.toString());
            e.printStackTrace();
            TextToSpeech.speech("エラーが起きたからもう一度聞くね", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
            Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
        }
    }

    final private static void backQs() {
        Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
        int backQsIndex = 1;
        final HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
        if (state.getQuestionI() == HabitQsState.QuestionI.IS_EXERCISE) {
            backQsIndex = 0;
            Store.dispatch(SotaState.class,SotaState.Action.UPDATE_MODE,SotaState.Mode.LISTEN_BACK_DAY);
        } else {
            if (state.getQuestionI() == HabitQsState.QuestionI.SLEEP) {
                if (!state.getResult().isEatSnack()) {
                    backQsIndex = 2;
                }
            }
        }
        final Enum<HabitQsState.QuestionI> questionI = HabitQsState.QuestionI.values()[state.getQuestionI().ordinal()
                - backQsIndex];
        Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_QUESTION_IDX, questionI);
    }

    final private static boolean nextQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish,
            CRecordMic mic, int backDay) {
        boolean isConformed = false;
        final HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
        // モード更新
        Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
        int qsIndex = state.getQuestionI().ordinal() + 1;
        if (state.getQuestionI() == HabitQsState.QuestionI.EAT_SNACK) {
            // お菓子食べてなかったら、お菓子の名前を聞くのはスキップ
            boolean isEat = state.getResult().isEatSnack();
            if (!isEat) {
                qsIndex = state.getQuestionI().ordinal() + 2;
            } else {
                qsIndex = state.getQuestionI().ordinal() + 1;
            }
        } else if (state
                .getQuestionI() == HabitQsState.QuestionI.values()[HabitQsState.QuestionI.values().length - 1]) {
            // 終了
            qsIndex = HabitQsState.QuestionI.values()[0].ordinal();
            // <結果を送信>
//            if (!sendResult(backDay)) {
//                TextToSpeech.speech("登録に失敗しました。", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
//            }
            // </結果を送信>
            isConformed = true;
        }
        final Enum<HabitQsState.QuestionI> questionI = HabitQsState.QuestionI.values()[qsIndex];

        Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_QUESTION_IDX, questionI);
        return isConformed;
    }

    final public static boolean sendResult(int backDay) {
        boolean isSuccess = false;
        FindNameState fnState = (FindNameState) Store.getState(FindNameState.class);
        // sotaと会話している人の名前を取得
        ArrayList<User> fnResults = fnState.getResults();
        String nickName = fnResults.get(fnResults.size() - 1).getNickName();
        final HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
        state.getResult().setNickName(nickName);
        state.getResult().setBackDay(backDay);
        try {
            PostHabitRes res = MyHttpCon.postHabit(state.getResult());
            boolean success = res.isSuccess();
            if (success) {
                CRobotUtil.Log(TAG, "登録成功");
                isSuccess = true;
            } else {
                CRobotUtil.Log(TAG, "登録失敗");
            }
        } catch (Exception e) {
            e.printStackTrace();
            CRobotUtil.Log(TAG, "失敗");
        }
        return isSuccess;
    }
}
