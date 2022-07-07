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
import jp.hayamiti.state.SpRecState;
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

public class HabitQs {
	static final String TAG = "HabitQs";
	static final String REC_PATH = "./test_rec.wav";
	static final String REC_START_SOUND = "sound/mao-damasi-onepoint23.wav";

	public static void main(String[] args) {
		MyLog.info(TAG, "test");
		ArrayList<State> stateList = new ArrayList<State>(){{
			add(new HabitQsState());
		}};
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
		PostHabitReq result = state.getResult();
			try {
				MyLog.info(TAG, "getResult: " + JSONMapper.mapper.writeValueAsString(result));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		MyLog.info(TAG, "end");
	}

	public static boolean habitQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish,
			CRecordMic mic, int backDay) {
		boolean isFinish = false;
		HabitQsState state = (HabitQsState) Store.getState(HabitQsState.class);
		Enum<HabitQsState.Mode> mode = state.getMode();
		Enum<HabitQsState.QuestionI> questionI = state.getQuestionI();
		PostHabitReq result = state.getResult();
		if (mode == HabitQsState.Mode.LISTEN_ANS) {
			// <質問をしてこたえを聞き取る>
			recordARec(mic, sotawish, questionI, backDay, motion);
			// </質問をしてこたえを聞き取る>
		} else if (mode == HabitQsState.Mode.CONFORM_ANS) {
			// <答えを確認>
			TextToSpeech.speech(state.getConformText() + "、であってる?", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			// モード更新
			Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.WAIT_CONFORM_ANS);
			// </答えを確認>
		} else if (mode == HabitQsState.Mode.WAIT_CONFORM_ANS) {
			// <確認待機>
			isFinish = watiConform(pose, mem, motion, sotawish, mic, questionI, result, backDay);
			// </確認待機>
		}

		return isFinish;
	}

	private static void recordARec(CRecordMic mic, MotionAsSotaWish sotawish, Enum<HabitQsState.QuestionI> questionI,int backDay,CSotaMotion motion) {
		String type = "";
		HabitQsState.Action action = null;
		String question = "";
		try {
			String relativeYesterday = backDay == 0 ?  "昨日" : (backDay+1) +"日前";
			String relativeToday = backDay == 0 ? "今日" : backDay + "日前";
			// <今聞こうとしている質問に合わせた値を代入する>
			switch ((HabitQsState.QuestionI) questionI) {
				case IS_EXERCISE:
					type = "exercise";
					action = HabitQsState.Action.SET_EXERCISE_LISTEN_RESULT;
					question = relativeYesterday + "運動した?";
					break;
				case IS_DRINKING:
					type = "drinking";
					action = HabitQsState.Action.SET_DRINGKING_LISTEN_RESULT;
					question = relativeYesterday + "お酒飲んだ?";
					break;
				case EAT_BREAKFAST:
					type = "eatBreakfast";
					question = relativeYesterday + "朝ごはん食べた?";
					action = HabitQsState.Action.SET_EATBREAKFAST_LISTEN_RESULT;
					break;
				case EAT_SNACK:
					type = "eatSnack";
					question = relativeYesterday + "おやつ食べた?";
					action = HabitQsState.Action.SET_EATSNACK_LISTEN_RESULT;
					break;
				case SNACK_NAME:
					type = "snackName";
					question = relativeYesterday + "おやつに何食べたの?";
					action = HabitQsState.Action.SET_SNACKNAME_LISTEN_RESULT;
					break;
				case SLEEP:
					type = "sleep";
					question = relativeYesterday + "何時に寝た？例えば午後8時に寝たなら20時に寝た、夜の1時に寝たなら25時に寝たと答えてね。";
					action = HabitQsState.Action.SET_SLEEP_LISTEN_RESULT;
					break;
				case GETUP:
					type = "getUp";
					question = relativeToday + "何時に起きた?答え方はさっきと同じでお願い。";
					action = HabitQsState.Action.SET_GETUP_LISTEN_RESULT;
					break;
			}
			// </今聞こうとしている質問に合わせた値を代入する>

			// 質問する
			TextToSpeech.speech(question,sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
			//音声ファイル再生
//			//raw　Waveファイルのみ対応
//			CPlayWave.PlayWave(REC_START_SOUND, false);
//			// <録音>
//			mic.startRecording(REC_PATH, 3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
			SpeechRec.speechRec(mic, motion);
			// apiサーバーに送信して、解析してもらう
			String result = MyHttpCon.habitQs(((SpRecState) Store.getState(SpRecState.class)).getResult(), type);
			HabitQsRes res = JSONMapper.mapper.readValue(result, HabitQsRes.class);
			CRobotUtil.Log(TAG, JSONMapper.mapper.writeValueAsString(res));
			String ans = res.getResult();

			if (ans.equals("error")) {
				TextToSpeech.speech("エラーが起きたからもう一度聞くね", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
			} else {
				Store.dispatch(HabitQsState.class, action, res);
				// 答えがあってるか確認するモードへ
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.CONFORM_ANS);

			}
		} catch (Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			e.printStackTrace();
			TextToSpeech.speech("エラーが起きたからもう一度聞くね",sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
		}
	}

	private static boolean watiConform(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, Enum<HabitQsState.QuestionI> questionI, PostHabitReq result,int backDay) {
		boolean isConformed = false;
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if (yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if (isYes) {
				// モード更新
				Store.dispatch(HabitQsState.class, HabitQsState.Action.UPDATE_MODE, HabitQsState.Mode.LISTEN_ANS);
				if (questionI == HabitQsState.QuestionI.EAT_SNACK) {
					// お菓子食べてなかったら、お菓子の名前を聞くのはスキップ
					boolean isEat = result.isEatSnack();
					if (!isEat) {
						questionI = HabitQsState.QuestionI.values()[questionI.ordinal()+2];
					} else {
						questionI = HabitQsState.QuestionI.values()[questionI.ordinal()+1];
					}
				} else if (questionI == HabitQsState.QuestionI.values()[HabitQsState.QuestionI.values().length - 1]) {
					// 終了
					questionI = HabitQsState.QuestionI.values()[0];
					// <結果を送信>
					if(!sendResult(result, backDay)) {
						TextToSpeech.speech("登録に失敗しました。", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
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

	private static boolean sendResult(PostHabitReq result, int backDay) {
	    boolean	isSuccess = false;
		FindNameState fnState = (FindNameState)Store.getState(FindNameState.class);
		// sotaと会話している人の名前を取得
		ArrayList<User> fnResults = fnState.getResults();
		String nickName = fnResults.get(fnResults.size() - 1).getNickName();
		result.setNickName(nickName);
		result.setBackDay(backDay);
        try {
	        PostHabitRes res = JSONMapper.mapper.readValue(MyHttpCon.postHabit(result), PostHabitRes.class);
		    boolean	success = res.isSuccess();
		 	if(success) {
		 		CRobotUtil.Log(TAG, "登録成功");
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
