package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.ConditionQsState;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.TextToSpeechSota;

public class ConditionQs {
	static final String TAG = "ConditionQs";
	static final String REC_PATH = "./test_rec.wav";

	public static boolean conditionQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		boolean isFinish = false;
		ConditionQsState state = (ConditionQsState)Store.getState(Store.CONDITION_QS_STATE);
		String mode = state.getMode();
		JSONObject result = state.getResult();
		if(mode == ConditionQsState.Mode.LISTEN_ANS) {
			// <質問をしてこたえを聞き取る>
			recordARecogByHttp(mic, sotawish);
			// </質問をしてこたえを聞き取る>
		}else if (mode == ConditionQsState.Mode.CONFORM_ANS) {
			// <答えを確認>
			sotawish.Say(result.getString("text") + "、であってる?");
			// モード更新
			Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.WAIT_CONFORM_ANS);
			// </答えを確認>
		}else if (mode == ConditionQsState.Mode.WAIT_CONFORM_ANS) {
			// <確認待機>
			String yesOrNoMode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
			if (yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
				boolean isYes = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getIsYes();
				if (isYes) {
					// モード更新
					Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
					// <結果を送信>
					FindNameState fnState = (FindNameState)Store.getState(Store.FIND_NAME_STATE);
					// sotaと会話している人の名前を取得
					ArrayList<JSONObject> fnResults = fnState.getResults();
					String nickName = fnResults.get(fnResults.size() - 1).getString("nickName");
					try {
						String res = MyHttpCon.postCondition(nickName, result.getString("result"), result.getString("text"));
						JSONObject data = new JSONObject(res);
					    boolean	success = data.getBoolean("success");
					 	if(success) {
					 		CRobotUtil.Log(TAG, "success");
					 	}
					} catch (Exception e) {
						e.printStackTrace();
						CRobotUtil.Log(TAG, "失敗");
					}
					// </結果を送信>
					isFinish = true;
				} else {
					// 聞き直す
					// モード更新
					Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
				}
			} else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
				// 確認しなおす
				// モード更新
				Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.CONFORM_ANS);
			}
			// yesOrNo処理
			YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
			// </確認待機>
		}
		return isFinish;
	}

	private static void recordARecogByHttp(CRecordMic mic, MotionAsSotaWish sotawish) {
		try {
			// 質問する
			sotawish.SayFile(TextToSpeechSota.getTTSFile("今日の体調はどんな感じ?"), MotionAsSotaWish.MOTION_TYPE_CALL);

			// <録音>
			mic.startRecording(REC_PATH, 10000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>

			String result = MyHttpCon.conditionQs(REC_PATH);
			CRobotUtil.Log(TAG, result);
			JSONObject data = new JSONObject(result);
			String ans = data.getString("result");
			CRobotUtil.Log(TAG, ans);

			if (ans.equals("error")) {
				sotawish.Say("エラーが起きたからもう一度聞くね");
				Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
			}else {
				Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.SET_LISTEN_RESULT, data);
				// 答えがあってるか確認するモードへ
				Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.CONFORM_ANS);
			}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			sotawish.Say("エラーが起きたからもう一度聞くね");
			Store.dispatch(Store.CONDITION_QS_STATE, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
		}
	}
}
