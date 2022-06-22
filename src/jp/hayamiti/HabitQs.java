package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import jp.hayamiti.httpCon.LifeHabit;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.TextToSpeechSota;

public class HabitQs {
	static final String TAG = "HabitQs";
	static final String REC_PATH = "./test_rec.wav";

	public static void main(String[] args) {
		MyLog.info(TAG, "test");
		ArrayList<State> stateList = new ArrayList<State>(){{
			add(new HabitQsState());
		}};
		Store.conbineState(stateList);
		// int count = HabitQsState.IS_EXERCISE;
		JSONObject yesNoAns = new JSONObject("{\"result\": true, \"text\": \"こんにちは\"}");

		JSONObject timeAns = new JSONObject("{\"result\": 20, \"text\": \"こんにちは\"}");
		JSONObject textAns = new JSONObject("{\"result\": \"チョコレート ポテトチップス\", \"text\": \"こんにちは\"}");
		Enum<HabitQsState.QuestionI>[] questionI = HabitQsState.QuestionI.values();
		for (Enum<HabitQsState.QuestionI> i : questionI) {
			switch ((HabitQsState.QuestionI)i) {
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
		ArrayList<JSONObject> result = state.getResult();
		for (Enum<HabitQsState.QuestionI> i : questionI) {
			MyLog.info(TAG, "getResult: " + result.get(i.ordinal()).toString());
		}

		MyLog.info(TAG, "end");
	}

	public static boolean habitQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish,
			CRecordMic mic) {
		boolean isFinish = false;
		HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
		Enum<HabitQsState.Mode> mode = state.getMode();
		// int qsNum = state.getQsNum();
		Enum<HabitQsState.QuestionI> questionI = state.getQuestionI();
		ArrayList<JSONObject> result = state.getResult();
		if (mode == HabitQsState.Mode.LISTEN_ANS) {
			// <質問をしてこたえを聞き取る>
			recordARecogByHttp(mic, sotawish, questionI);
			// </質問をしてこたえを聞き取る>
		} else if (mode == HabitQsState.Mode.CONFORM_ANS) {
			// <答えを確認>
			sotawish.Say(result.get(questionI.ordinal()).getString("result") + "、であってる?");
			// モード更新
			Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.WAIT_CONFORM_ANS);
			// </答えを確認>
		} else if (mode == HabitQsState.Mode.WAIT_CONFORM_ANS) {
			// <確認待機>
			isFinish = watiConform(pose, mem, motion, sotawish, mic, questionI, result);
			// </確認待機>
		}

		return isFinish;
	}

	private static void recordARecogByHttp(CRecordMic mic, MotionAsSotaWish sotawish, Enum<HabitQsState.QuestionI> questionI) {
		String type = "";
		HabitQsState.Action action = null;
		String question = "";
		try {
			// <今聞こうとしている質問に合わせた値を代入する>
			switch ((HabitQsState.QuestionI) questionI) {
				case IS_EXERCISE:
					type = "exercise";
					action = HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT;
					question = "昨日運動した?";
					break;
				case IS_DRINKING:
					type = "drinking";
					action = HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT;
					question = "昨日お酒飲んだ?";
					break;
				case EAT_BREAKFAST:
					type = "eatBreakfast";
					question = "昨日朝ごはん食べた?";
					action = HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT;
					break;
				case EAT_SNACK:
					type = "eatSnack";
					question = "昨日おやつ食べた?";
					action = HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT;
					break;
				case SNACK_NAME:
					type = "snackName";
					question = "昨日おやつに何食べたの?";
					action = HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT;
					break;
				case SLEEP:
					type = "sleep";
					question = "昨日何時に寝た？例えば午後8時に寝たなら20時に寝た、夜の1時に寝たなら25時に寝たと答えてね。";
					action = HabitQsState.Action.SET_SLEEP_LISTEN_RESULT;
					break;
				case GETUP:
					type = "getUp";
					question = "今日何時に起きた?答え方はさっきと同じでお願い。";
					action = HabitQsState.Action.SET_GETUP_LISTEN_RESULT;
					break;
			}
			// </今聞こうとしている質問に合わせた値を代入する>

			// 質問する
			sotawish.SayFile(TextToSpeechSota.getTTSFile(question), MotionAsSotaWish.MOTION_TYPE_CALL);

			// <録音>
			mic.startRecording(REC_PATH, 3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>

			// apiサーバーに送信して、解析してもらう
			String result = MyHttpCon.habitQs(REC_PATH, type);
			CRobotUtil.Log(TAG, result);
			JSONObject data = new JSONObject(result);
			String ans = data.getString("result");
			CRobotUtil.Log(TAG, ans);

			if (ans.equals("error")) {
				sotawish.Say("エラーが起きたからもう一度聞くね");
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
			} else {
				Store.dispatch(HabitQsState.class, action, data);
				// 答えがあってるか確認するモードへ
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);

			}
		} catch (Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			sotawish.Say("エラーが起きたからもう一度聞くね");
			Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
		}
	}

	private static boolean watiConform(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, Enum<HabitQsState.QuestionI> questionI, ArrayList<JSONObject> result) {
		boolean isConformed = false;
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if (yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if (isYes) {
				// モード更新
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
				if (questionI == HabitQsState.QuestionI.EAT_SNACK) {
					// お菓子食べてなかったら、お菓子の名前を聞くのはスキップ
					String isEat = result.get(questionI.ordinal()).getString("result");
					if (!isEat.equals("yes")) {
						questionI = HabitQsState.QuestionI.values()[questionI.ordinal()+2];
					} else {
						questionI = HabitQsState.QuestionI.values()[questionI.ordinal()+1];
					}
				} else if (questionI == HabitQsState.QuestionI.GETUP) {
					// 終了
					questionI = HabitQsState.QuestionI.IS_EXERCISE;
					// <結果を送信>
					if(!sendResult(result)) {
						sotawish.Say("登録に失敗しました。");
					}
					// </結果を送信>
					isConformed =true;
				} else {
					questionI = HabitQsState.QuestionI.values()[questionI.ordinal()+1];
				}
				Store.dispatch(HabitQsState.class, HabitQsState.Action.SET_QUESTION_IDX, questionI);
			} else {
				// 聞き直す
				// モード更新
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
			}
		} else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
			// 確認しなおす
			// モード更新
			Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);
		}
		// yesOrNo処理
		YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
		return isConformed;
	}

	private static boolean sendResult(ArrayList<JSONObject> result) {
	    boolean	isSuccess = false;
		FindNameState fnState = (FindNameState)Store.getState(FindNameState.class);
		// sotaと会話している人の名前を取得
		ArrayList<JSONObject> fnResults = fnState.getResults();
		String nickName = fnResults.get(fnResults.size() - 1).getString("nickName");
    	LifeHabit lifeHabit = new LifeHabit();
        int sleepTime = Integer.parseInt(result.get(HabitQsState.QuestionI.SLEEP.ordinal()).getString("result"));
        int getUpTime = Integer.parseInt(result.get(HabitQsState.QuestionI.GETUP.ordinal()).getString("result"));
        lifeHabit.setVal(
        		sleepTime,
        		getUpTime,
        		result.get(HabitQsState.QuestionI.IS_EXERCISE.ordinal()).getString("result").equals("yes"),
        		result.get(HabitQsState.QuestionI.IS_DRINKING.ordinal()).getString("result").equals("yes"),
        		result.get(HabitQsState.QuestionI.EAT_BREAKFAST.ordinal()).getString("result").equals("yes"),
        		result.get(HabitQsState.QuestionI.EAT_SNACK.ordinal()).getString("result").equals("yes"),
        		result.get(HabitQsState.QuestionI.SNACK_NAME.ordinal()).getString("result"));
        lifeHabit.setText(
        		result.get(HabitQsState.QuestionI.SLEEP.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.GETUP.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.IS_EXERCISE.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.IS_DRINKING.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.EAT_BREAKFAST.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.EAT_SNACK.ordinal()).getString("text"),
        		result.get(HabitQsState.QuestionI.SNACK_NAME.ordinal()).getString("text"));
        try {
	        String res = MyHttpCon.postHabit(nickName, lifeHabit);
		 	JSONObject data = new JSONObject(res);
		    boolean	success = data.getBoolean("success");
		 	if(success) {
		 		CRobotUtil.Log(TAG, "success");
		 		isSuccess = true;
		 	}else {
		 		CRobotUtil.Log(TAG, "登録失敗");
		 	}
        }catch (Exception e) {
        	e.printStackTrace();
        	CRobotUtil.Log(TAG, "失敗");
        }
        return isSuccess;
	}
}
