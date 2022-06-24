package jp.hayamiti;

import java.util.ArrayList;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.SpRecRes;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class SpeechRec {
	static final String TAG = "SpeechRec";
	static final String TEST_REC_PATH = "./test_rec.wav";
	public static void main(String[] args) {
		CRobotPose pose = null;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		//Sota用スピーチ認識クラス
//		SpeechRecog recog = new SpeechRecog(motion);
		//sotawish初期化
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);

		//マイク
		CRecordMic mic = new CRecordMic();
		try {
			//Store 初期化 stateを束ねる
			ArrayList<State> stateList = new ArrayList<State>() {{
				add(new SotaState());
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
						recordARecogByHttp(mic);
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
					}
					else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = sotaState.getSpRecResult();
						if(recordResult != ""){
							sotawish.StopIdling();
							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
									sotawish.Say("終了するよ", MotionAsSotaWish.MOTION_TYPE_BYE);
								break;
							}else {
								sotawish.Say(recordResult);
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
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

	/**
	 * 音声認識
	 * @param mic
	 */
	public static void recordARecogByHttp(CRecordMic mic) {
		try {
			// <録音>
			mic.startRecording(TEST_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			String result = MyHttpCon.speechRec(TEST_REC_PATH);
			CRobotUtil.Log(TAG, result);
//			JSONObject data = new JSONObject(result);
			SpRecRes res = JSONMapper.mapper.readValue(result, SpRecRes.class);
			MyLog.info(TAG,"get audio:" + res.result);
            Store.dispatch(SotaState.class, SotaState.Action.UPDATE_SP_REC_RESULT, res.result);

		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			Store.dispatch(SotaState.class, SotaState.Action.UPDATE_SP_REC_RESULT,"");


		}
	}

	/**
	 * 聞き取り
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 *
	 * @return boolean
	 */
//	public static boolean speechRec(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, int maxCount) {
//		String mode = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getMode();
//		String result = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getResult();
//		int count = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getCount();
//		if(mode == SpeechRecState.Mode.NON_LISTENING) {
//			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.PASSIVE_LISTENING);
//		}else if(mode == SpeechRecState.Mode.PASSIVE_LISTENING) {
//			MyLog.info(TAG, "passive listening");
//			recordForSpRec(mic);
//		}
//		else if(mode == SpeechRecState.Mode.ACTIVE_LISTENING) {
//			MyLog.info(TAG, "active listening");
//			sotawish.StartIdling();
//			recordForSpRec(mic);
//		}else if(mode == SpeechRecState.Mode.FINISH_LISTENING) {
//			MyLog.info(TAG, "finish listening");
//			sotawish.StopIdling();
//			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.NON_LISTENING);
//			return true;
//		}
//		if(count == maxCount) {
//			MyLog.info(TAG, "finish listening by maxCount");
//			sotawish.StopIdling();
//			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.NON_LISTENING);
//			return true;
//		}
//		return false; // 終わってない
//
//	}

//	private static void recordForSpRec(CRecordMic mic) {
//		try {
//			// <録音>
//			mic.startRecording(TEST_REC_PATH,3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
//			String mode = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getMode();
//			if(mode == SpeechRecState.Mode.NON_LISTENING) {
//				return;
//			}
//			// <録音した音声をサーバーに送信できる形にエンコード>
//			File audioFile = new File(
//					TEST_REC_PATH);
//	        byte[] bytes = FileUtils.readFileToByteArray(audioFile);
//	        String encoded = Base64.getEncoder().encodeToString(bytes);
//	        CRobotUtil.Log(TAG, "encoded record file");
//	        // </録音した音声をサーバーに送信できる形にエンコード>
//	        boolean isAdditional =  false;
//	        if(((SpeechRecState)Store.getState(Store.SPEECH_REC_STATE)).getCount() > 0) {
//	        	isAdditional = true;
//	        }
//	        String payload = "{\"sound\":"
//                    + encoded.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>")
//                    .replace("=", "<EQUAL>").replace(",", "<COMMA>")
//            + ", \"isAdditional\":" + isAdditional
//            + ", \"sendTime\":" + System.currentTimeMillis()
//            + "}";
//	        // 送信
//	        MyWsClient.emit(SpeechRecListener.CHANNEL, payload);
//		}catch(Exception e) {
//			CRobotUtil.Log(TAG, e.toString());
//		}
//	}
}
