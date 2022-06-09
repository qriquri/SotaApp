package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.HabitQsState;
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
//		int count = HabitQsState.IS_EXERCISE;
		JSONObject	yesNoAns = new JSONObject("{\"result\": true, \"text\": \"こんにちは\"}");

		JSONObject	timeAns = new JSONObject("{\"result\": 20, \"text\": \"こんにちは\"}");
		JSONObject	textAns = new JSONObject("{\"result\": \"チョコレート ポテトチップス\", \"text\": \"こんにちは\"}");
		for(int i = HabitQsState.IS_EXERCISE; i < HabitQsState.GETUP + 1; i++) {
			switch (i) {
			case HabitQsState.IS_EXERCISE:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT, yesNoAns);
				break;
			case HabitQsState.IS_DRINKING:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT, yesNoAns);
				break;
			case HabitQsState.EAT_BREAKFAST:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT, yesNoAns);
				break;
			case HabitQsState.EAT_SNACK:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT, yesNoAns);
				break;
			case HabitQsState.SNACK_NAME:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT, textAns);
				break;
			case HabitQsState.SLEEP:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_SLEEP_LISTEN_RESULT, timeAns);
				break;
			case HabitQsState.GETUP:
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_GETUP_LISTEN_RESULT, timeAns);
				break;
			}
		}
		HabitQsState state = (HabitQsState) Store.getState(Store.HABIT_QS_STATE);
		ArrayList<JSONObject> result = state.getResult();
		for(int i = HabitQsState.IS_EXERCISE; i < HabitQsState.GETUP + 1; i++) {
			MyLog.info(TAG, "getResult: " + result.get(i).toString());
		}

		MyLog.info(TAG, "end");
	}

	public static boolean habitQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		boolean isFinish = false;
		HabitQsState state = (HabitQsState) Store.getState(Store.HABIT_QS_STATE);
		String mode = state.getMode();
		int qsNum = state.getQsNum();
		ArrayList<JSONObject> result = state.getResult();
			if(mode == HabitQsState.Mode.LISTEN_ANS) {
				recordForHabitQsByHttp(mic, sotawish, qsNum);
			}else if(mode == HabitQsState.Mode.WAIT_SERVER_RES) {

			}else if(mode == HabitQsState.Mode.CONFORM_ANS) {
				sotawish.Say(result.get(qsNum).getString("result") + "、であってる?");
				// モード更新
				Store.dispatch(Store.FIND_NAME_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.WAIT_CONFORM_ANS);
			}else if(mode == HabitQsState.Mode.WAIT_CONFORM_ANS) {
				// 確認待機
				String yesOrNoMode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
				if(yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
					boolean isYes = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getIsYes();
					if(isYes) {
						// モード更新
						Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
						if (qsNum == HabitQsState.EAT_SNACK) {
							// お菓子食べてなかったら、お菓子の名前を聞くのはスキップ
							String isEat = result.get(qsNum).getString("result");
							if(!isEat.equals("yes")) {
								qsNum+= 2;
							}
						}else if (qsNum == HabitQsState.GETUP) {
							// 終了
							qsNum = HabitQsState.IS_EXERCISE;
							isFinish = true;
						}else {
							qsNum++;
						}
						Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.SET_QUESTION_NUM, qsNum);
					}else {
						// 聞き直す
						// モード更新
						Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
					}
				}else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
					// 確認しなおす
					// モード更新
					Store.dispatch(Store.FIND_NAME_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);
				}
				// yesOrNo処理
				YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
			}

		return isFinish;
	}

	private static void recordForHabitQsByHttp(CRecordMic mic, MotionAsSotaWish sotawish, int qsNum) {
		try {
			String type = "";
			String action = "";
			String question = "";
			switch (qsNum){
			case HabitQsState.IS_EXERCISE:
				type = "exercise";
				action = HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT;
				question = "昨日運動した?";
				break;
			case HabitQsState.IS_DRINKING:
				type = "drinking";
				action = HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT;
				question = "昨日お酒飲んだ?";
				break;
			case HabitQsState.EAT_BREAKFAST:
				type = "eatBreakfast";
				question = "昨日朝ごはん食べた?";
				action = HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT;
				break;
			case HabitQsState.EAT_SNACK:
				type = "eatSnack";
				question = "昨日おやつ食べた?";
				action = HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT;
				break;
			case HabitQsState.SNACK_NAME:
				type = "snackName";
				question = "昨日おやつに何食べたの?";
				action = HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT;
				break;
			case HabitQsState.SLEEP:
				type = "sleep";
				question = "昨日何時に寝た？例えば午後8時に寝たなら20時に寝た、と答えてね。";
				action = HabitQsState.Action.SET_SLEEP_LISTEN_RESULT;
				break;
			case HabitQsState.GETUP:
				type = "getUp";
				question = "今日何時に起きた?答え方はさっきと同じでお願い。";
				action = HabitQsState.Action.SET_GETUP_LISTEN_RESULT;
				break;
			}

			sotawish.SayFile(TextToSpeechSota.getTTSFile(question),MotionAsSotaWish.MOTION_TYPE_CALL);

			// <録音>
			mic.startRecording(REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>

			String result = MyHttpCon.habitQs(REC_PATH, type);
			CRobotUtil.Log(TAG, result);
			JSONObject data = new JSONObject(result);
			String ans = data.getString("result");
			CRobotUtil.Log(TAG, ans);

			if(ans.equals("error")) {
				sotawish.Say("エラーが起きたからもう一度聞くね");
				Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
			}else {
	    		// 追加する
	        	Store.dispatch(Store.HABIT_QS_STATE, action, data);
	        	Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);

			}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			sotawish.Say("エラーが起きたからもう一度聞くね");
			Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
		}
	}
}
