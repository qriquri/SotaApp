package jp.hayamiti;

import java.io.IOException;
import java.util.ArrayList;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.GetSuggestedNextHabitRes;
import jp.hayamiti.httpCon.DbCom.GetHabitsRes;
import jp.hayamiti.httpCon.DbCom.PostSuggestedHabitReq;
import jp.hayamiti.httpCon.DbCom.PostSuggestedHabitRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.SuggestNextHabitState;
import jp.hayamiti.state.TextToSpeechState;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.utils.MyStrBuilder;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class SuggestNextHabit {
    private static final String TAG = "SuggestNextHabit";

    public static void main(String[] args) {
        try {
            // </JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
            CRobotPose pose = null;
            // VSMDと通信ソケット・メモリアクセス用クラス
            CRobotMem mem = new CRobotMem();
            // Sota用モーション制御クラス
            CSotaMotion motion = new CSotaMotion(mem);
            // sotawish初期化
            MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
            // マイク
            CRecordMic mic = new CRecordMic();
            // Store 初期化 stateを束ねる
            ArrayList<State> stateList = new ArrayList<State>() {
                {
                    add(new SotaState());
                    add(new FindNameState());

                    add(new TextToSpeechState());
                    add(new SuggestNextHabitState());
                }
            };
            Store.bind(stateList);
            // 音声合成手法を設定
            Store.dispatch(TextToSpeechState.class, TextToSpeechState.Action.SET_METHOD,
                    TextToSpeechState.Method.SOTA_CLOUD);
            final User testUser = new User();
            testUser.setNickName("フー");
            Store.dispatch(FindNameState.class, FindNameState.Action.ADD_NAME, testUser);
            Store.dispatch(SuggestNextHabitState.class, SuggestNextHabitState.Action.UPDATE_MODE,
                    SuggestNextHabitState.Mode.SUGGEST);
            final GetHabitsRes res = MyHttpCon.getOneWeekHabits("フー", true, 1);
            MyLog.info(TAG, res.getResults().toString());
            MyLog.info(TAG, MyStrBuilder.build(32, res.getResults().size(), "個取得"));
            if (mem.Connect()) {
                // Sota仕様にVSMDを初期化
                motion.InitRobot_Sota();
                motion.ServoOn();
                suggestNextHabit(pose, mem, motion, sotawish, mic);
            }
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

    }

    /**
     *
     * @param pose
     * @param mem
     * @param motion
     * @param sotawish
     * @param mic
     */
    final public static void suggestNextHabit(CRobotPose pose, CRobotMem mem, CSotaMotion motion,
            MotionAsSotaWish sotawish, CRecordMic mic) {
        // String mode = ((YesOrNoState)
        // Store.getState(Store.YES_OR_NO_STATE)).getMode();
        final Enum<SuggestNextHabitState.Mode> mode = ((SuggestNextHabitState) Store
                .getState(SuggestNextHabitState.class))
                        .getMode();
        if (mode == SuggestNextHabitState.Mode.SUGGEST) {
            // sotaと会話している人の名前を取得
            final FindNameState findNameState = (FindNameState) Store.getState(FindNameState.class);
            final ArrayList<User> fnResults = findNameState.getResults();
            final String nickName = fnResults.get(fnResults.size() - 1).getNickName();
            try {
                // 先週の生活習慣を集計する
                final GetHabitsRes res = MyHttpCon.getOneWeekHabits(nickName, true, 1);
                if (res.getResults().size() < 8) {
                    TextToSpeech.speech("生活習慣が集まったら改善目標を提案するね。", sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
                    return;
                }
                if (MyHttpCon.getSuggestedHabit(nickName, 1).getResults().size() != 0) {
                    CRobotUtil.Log(TAG, "今日は改善目標言わない");
                    return;
                }
                int[] habit = new int[5];
                for (int i = 1, length = res.getResults().size(); i < length; i++) {
                    // TODO Enum化
                    habit[0] += res.getResults().get(i).isExercise() ? 1 : 0;
                    habit[1] += res.getResults().get(i).isDrinking() ? 1 : 0;
                    habit[2] += res.getResults().get(i).isEatBreakfast() ? 1 : 0;
                    habit[3] += res.getResults().get(i).isEatSnack() ? 1 : 0;
                    habit[4] += res.getResults().get(i).getGetUp() - (res.getResults().get(i).getSleep() - 24);
                }
                // 小数点以下が切り捨てられるから、とりあえずまとめてから割り算する
                habit[4] /= res.getResults().size() - 1;
                // 改善目標を得る
                final GetSuggestedNextHabitRes resNextHabit = MyHttpCon.getSuggestedNextHabit(habit);
                // TODO もっといい名前考えて
                String name = "";
                switch (resNextHabit.getIndex()) {
                    case 0:
                        name = "運動した日数";
                        break;
                    case 1:
                        name = "お酒飲んだ日数";
                        break;
                    case 2:
                        name = "朝食食べた日数";
                        break;
                    case 3:
                        name = "おやつ食べた日";
                        break;
                    case 4:
                        name = "平均睡眠時間";
                        break;
                }
                int value = resNextHabit.getValue() - habit[resNextHabit.getIndex()];

                if (!sendResult("" + resNextHabit.getIndex(), value)) {
                    TextToSpeech.speech("送信に失敗したよ。", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
                    return;
                }

                String action = "増やそう";
                if (value < 0) {
                    value *= -1;
                    action = "減らそう";
                }
                String unit = "日";
                if (resNextHabit.getIndex() == 4) {
                    unit = "時間";
                }
                // 改善目標を言う
                String sentence = MyStrBuilder.build(124, "先週の", name, "は", habit[resNextHabit.getIndex()], unit,
                        "だったよ。");
                TextToSpeech.speech(sentence, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
                sentence = MyStrBuilder.build(124, "今週は", value, unit, action);
                TextToSpeech.speech(sentence, sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);

            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
                TextToSpeech.speech("通信に失敗したよ", sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
                return;
            }

        }
    }

    final private static boolean sendResult(String name, int value) {
        boolean isSuccess = false;
        try {
            FindNameState fnState = (FindNameState) Store.getState(FindNameState.class);
            // sotaと会話している人の名前を取得
            ArrayList<User> fnResults = fnState.getResults();
            String nickName = fnResults.get(fnResults.size() - 1).getNickName();
            PostSuggestedHabitReq req = new PostSuggestedHabitReq();
            req.setNickName(nickName);
            req.setItem(name);
            req.setValue(value);
            PostSuggestedHabitRes res = MyHttpCon.postSuggestedHaibt(req);
            boolean success = res.isSuccess();
            if (success) {
                CRobotUtil.Log(TAG, "登録成功");
                isSuccess = true;
            } else {
                CRobotUtil.Log(TAG, "登録失敗");
                isSuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            CRobotUtil.Log(TAG, "失敗");
            isSuccess = false;
        }

        return isSuccess;
    }
}
