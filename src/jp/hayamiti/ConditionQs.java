package jp.hayamiti;

import java.util.ArrayList;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.ConditionQsRes;
import jp.hayamiti.httpCon.DbCom.PostConditionReq;
import jp.hayamiti.httpCon.DbCom.PostConditionRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.ConditionQsState;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
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
	static final String REC_START_SOUND = "sound/mao-damasi-onepoint23.wav";

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
			Store.bind(stateList);

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
					if(conditionQs(pose, mem, motion, sotawish, mic, 0)) {
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


	public static boolean conditionQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, int backDay) {
		boolean isFinish = false;
		ConditionQsState state = (ConditionQsState)Store.getState(ConditionQsState.class);
		Enum<ConditionQsState.Mode> mode = state.getMode();
		ConditionQsRes result = state.getResult();
		if(mode == ConditionQsState.Mode.LISTEN_ANS) {
			// <質問をしてこたえを聞き取る>
			recordARec(mic, sotawish, backDay, motion);
			// </質問をしてこたえを聞き取る>
		}else if (mode == ConditionQsState.Mode.CONFORM_ANS) {
			// <答えを確認>
			sotawish.Say(result.getText() + "、であってる?");
			// モード更新
			Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.WAIT_CONFORM_ANS);
			// </答えを確認>
		}else if (mode == ConditionQsState.Mode.WAIT_CONFORM_ANS) {
			// <確認待機>
			isFinish = waitConform(pose, mem, motion, sotawish, mic, result, backDay);
			// </確認待機>
		}
		return isFinish;
	}

	private static void recordARec(CRecordMic mic, MotionAsSotaWish sotawish, int backDay,CSotaMotion motion) {
		try {
			String relativeToday = backDay == 0 ? "今日" : backDay + "日前";
			// 質問する
			sotawish.SayFile(TextToSpeechSota.getTTSFile(relativeToday + "の体調はどんな感じ?"), MotionAsSotaWish.MOTION_TYPE_CALL);
			//音声ファイル再生
//			//raw　Waveファイルのみ対応
//			CPlayWave.PlayWave(REC_START_SOUND, false);
//			// <録音>
//			mic.startRecording(REC_PATH, 5000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
			SpeechRec.speechRec(mic, motion);
			String result = MyHttpCon.conditionQs(((SpRecState) Store.getState(SpRecState.class)).getResult());
			CRobotUtil.Log(TAG, result);
//			JSONObject data = new JSONObject(result);
			ConditionQsRes res = JSONMapper.mapper.readValue(result, ConditionQsRes.class);
			String ans = res.getResult();
			CRobotUtil.Log(TAG, ans);

			if (ans.equals("error")) {
				sotawish.Say("エラーが起きたからもう一度聞くね");
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
			}else {
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.SET_LISTEN_RESULT, res);
				// 答えがあってるか確認するモードへ
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.CONFORM_ANS);
			}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			sotawish.Say("エラーが起きたからもう一度聞くね");
			Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
		}
	}

	private static boolean waitConform(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, ConditionQsRes result, int backDay) {
		boolean isConformed = false;
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if (yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if (isYes) {
				// モード更新
				Store.dispatch(ConditionQsState.class, ConditionQsState.Action.UPDATE_MODE, ConditionQsState.Mode.LISTEN_ANS);
				// <結果を送信>
				if(!sendResult(result, backDay)) {
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

	private static boolean sendResult(ConditionQsRes result, int backDay) {
		boolean isSuccess = false;
		try {
			FindNameState fnState = (FindNameState)Store.getState(FindNameState.class);
			// sotaと会話している人の名前を取得
			ArrayList<User> fnResults = fnState.getResults();
			String nickName = fnResults.get(fnResults.size() - 1).getNickName();
			PostConditionReq req = new PostConditionReq();
			req.setNickName(nickName);
			req.setSentence(result.getText());
			req.setCondition(result.getResult());
			req.setBackDay(backDay);
			PostConditionRes res = JSONMapper.mapper.readValue(MyHttpCon.postCondition(req), PostConditionRes.class);
		    boolean	success = res.isSuccess();
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
