package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.ConditionQsState;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.State;
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

	public static void main(String[] args) {
		CRobotPose pose = null;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
		//マイク
		CRecordMic mic = new CRecordMic();
		try {
			//Store 初期化 stateを束ねる
			ArrayList<State> stateList = new ArrayList<State>() {{
				add(new SotaState());
				add(new FindNameState());
				add(new YesOrNoState());
				add(new ConditionQsState());
			}};
			Store.conbineState(stateList);

	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(SotaState.class);
			// </stateの取得>
			// sotaのモードを取得
			Enum<SotaState.Mode> mode = sotaState.getMode();
			if(mem.Connect()){
				//Sota仕様にVSMDを初期化
				motion.InitRobot_Sota();
				// サーボモーターをon
				motion.ServoOn();
				// 気を付けのポーズに戻す
				MotionSample.defaultPose(pose, mem, motion);
				// sotaのモードをlisteningに変化
				Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
				while(true){
					// モード取得
					mode = sotaState.getMode();
					if(conditionQs(pose, mem, motion, sotawish, mic)) {
						sotawish.Say("終了するよ");
						break;
					}

				}				}
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			e.printStackTrace();
		}finally {
			GamingLED.off(pose, mem, motion);
			motion.ServoOff();
		}
	}


	public static boolean conditionQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		boolean isFinish = false;
		ConditionQsState state = (ConditionQsState)Store.getState(ConditionQsState.class);
		Enum<ConditionQsState.Mode> mode = state.getMode();
		JSONObject result = state.getResult();
		if(mode == ConditionQsState.Mode.LISTEN_ANS) {
			// <質問をしてこたえを聞き取る>
			recordARecogByHttp(mic, sotawish);
			// </質問をしてこたえを聞き取る>
		}else if (mode == ConditionQsState.Mode.CONFORM_ANS) {
			// <答えを確認>
			sotawish.Say(result.getString("text") + "、であってる?");
			// モード更新
			Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.WAIT_CONFORM_ANS);
			// </答えを確認>
		}else if (mode == ConditionQsState.Mode.WAIT_CONFORM_ANS) {
			// <確認待機>
			isFinish = waitConform(pose, mem, motion, sotawish, mic, result);
			// </確認待機>
		}
		return isFinish;
	}

	private static void recordARecogByHttp(CRecordMic mic, MotionAsSotaWish sotawish) {
		try {
			// 質問する
			sotawish.SayFile(TextToSpeechSota.getTTSFile("今日の体調はどんな感じ?"), MotionAsSotaWish.MOTION_TYPE_CALL);

			// <録音>
			mic.startRecording(REC_PATH, 5000);
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
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
			}else {
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.SET_LISTEN_RESULT, data);
				// 答えがあってるか確認するモードへ
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.CONFORM_ANS);
			}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			sotawish.Say("エラーが起きたからもう一度聞くね");
			Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
		}
	}

	private static boolean waitConform(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, JSONObject result) {
		boolean isConformed = false;
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if (yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if (isYes) {
				// モード更新
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
				// <結果を送信>
				if(!sendResult(result)) {
					sotawish.Say("送信に失敗したよ。");
				}
				// </結果を送信>
				isConformed = true;
			} else {
				// 聞き直す
				// モード更新
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
			}
		} else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
			// 確認しなおす
			// モード更新
			Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.CONFORM_ANS);
		}
		// yesOrNo処理
		YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
		return isConformed;
	}

	private static boolean sendResult(JSONObject result) {
		boolean isSuccess = false;
		try {
			FindNameState fnState = (FindNameState)Store.getState(FindNameState.class);
			// sotaと会話している人の名前を取得
			ArrayList<JSONObject> fnResults = fnState.getResults();
			String nickName = fnResults.get(fnResults.size() - 1).getString("nickName");
			String res = MyHttpCon.postCondition(nickName, result.getString("result"), result.getString("text"));
			JSONObject data = new JSONObject(res);
		    boolean	success = data.getBoolean("success");
		 	if(success) {
		 		CRobotUtil.Log(TAG, "登録成功");
		 		isSuccess = true;
		 	}else {
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