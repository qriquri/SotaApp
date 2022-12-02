package jp.hayamiti;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.BasicRes;
import jp.hayamiti.httpCon.ApiCom.DayQsRes;
import jp.hayamiti.state.DayQsState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.utils.MyStrBuilder;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

final public class DayQs {
	private static final String TAG = "DayQs";

	final public static boolean dayQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		boolean isFinish = false;
		DayQsState state = (DayQsState)Store.getState(DayQsState.class);
		Enum<DayQsState.Mode> mode = state.getMode();
		if(mode == DayQsState.Mode.LISTEN_ANS) {
			isFinish = recordARec(mic, sotawish, motion);
		}else if(mode == DayQsState.Mode.CONFORM_ANS) {
			int backDay = state.getResult();
			if(backDay == 0) {
				TextToSpeech.speech("今日のデータを登録するよ", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			}else if (backDay == 1) {
				TextToSpeech.speech("昨日のデータを登録するよ", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			}
			else {
				TextToSpeech.speech(MyStrBuilder.build(64, backDay,"日前のデータを登録するよ"), sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			}
			Store.dispatch(DayQsState.class, DayQsState.Action.UPDATE_MODE, DayQsState.Mode.WAIT_CONFORM);
		}else if(mode == DayQsState.Mode.WAIT_CONFORM) {
			isFinish = nextQs(pose, mem, motion, sotawish, mic);
		}
		return isFinish;
	}

	private static boolean recordARec(CRecordMic mic, MotionAsSotaWish sotawish, CSotaMotion motion) {
		try {
			TextToSpeech.speech("何日前のデータを登録する？ また、終了する場合は、終わりと答えてね。",sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);

			SpeechRec.speechRec(mic, motion);
			DayQsRes res = MyHttpCon.dayRec(((SpRecState) Store.getState(SpRecState.class)).getResult());
            CRobotUtil.Log(TAG, res.toString());
			String ans = res.getResult();
			String text = res.getText();
			if(text.contains("おわり") || text.contains("終わり")){
				// 終了と答えたことを登録する
				Store.dispatch(DayQsState.class, DayQsState.Action.SET_IS_END, true);
				// 質問のループを終わらせるためにtrueを返す
				return true;
			}
			CRobotUtil.Log(TAG, ans);
			if(ans.equals("error")) {
				TextToSpeech.speech("エラーが起きたからもう一度聞くね", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
				Store.dispatch(DayQsState.class, DayQsState.Action.UPDATE_MODE, DayQsState.Mode.LISTEN_ANS);
			}else {
	    		// 追加する
	        	Store.dispatch(DayQsState.class, DayQsState.Action.SET_RESULT, Integer.parseInt(ans));
	            Store.dispatch(DayQsState.class, DayQsState.Action.UPDATE_MODE, DayQsState.Mode.CONFORM_ANS);
	            // これ入れないと二人目のときにtrueを返しちゃう
				Store.dispatch(DayQsState.class, DayQsState.Action.SET_IS_END, false);
			}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			e.printStackTrace();
			TextToSpeech.speech("エラーが起きたからもう一度聞くね", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
            Store.dispatch(DayQsState.class, DayQsState.Action.UPDATE_MODE, DayQsState.Mode.LISTEN_ANS);

		}
		return false;
	}

	final public static boolean nextQs(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		boolean isConformed = true;
		Store.dispatch(DayQsState.class, DayQsState.Action.UPDATE_MODE, DayQsState.Mode.LISTEN_ANS);
		return isConformed;
	}

	final public static void main(String[] args) {
		// <JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
		try {
			JSONMapper.mapper.writeValueAsString(new BasicRes());
		} catch (JsonProcessingException e) {
			// TODO 自動生成された catch ブロック
			MyLog.error(TAG, e.toString());
		}
		// </JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
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
				add(new DayQsState());
				add(new YesOrNoState());
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
					if(mode == SotaState.Mode.LISTENING) {
				        SpeechRec.recordARecogByHttp(mic);
						// モード更新
				        Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
					}else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = sotaState.getSpRecResult();
						if(recordResult != ""){
							sotawish.StopIdling();
							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
								TextToSpeech.speech("終了するよ", sotawish, MotionAsSotaWish.MOTION_TYPE_BYE);
								// 通信終了
//								client.disconnect();
								break;
							}else if(recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTEN_BACK_DAY);
							}else {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
						}else {
							// モード更新
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
						}
					}else if(mode == SotaState.Mode.LISTEN_BACK_DAY) {
						if(dayQs(pose, mem, motion, sotawish, mic)) {
						// モード更新
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);

						}
					}
				}
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
		}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			e.printStackTrace();
		}finally {
			GamingLED.off(pose, mem, motion);
			motion.ServoOff();
		}
	}
}
