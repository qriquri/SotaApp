package jp.hayamiti;

import java.io.File;
import java.net.URI;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpeechRecState;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.websocket.FindNameListener;
import jp.hayamiti.websocket.MessageListener;
import jp.hayamiti.websocket.MyWsClient;
import jp.hayamiti.websocket.SpeechRecListener;
import jp.hayamiti.websocket.YesOrNoListener;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class SpeechRec {
	static final String TAG = "FindNmae";
	static final String TEST_REC_PATH = "./test_rec.wav";
	static final String FIND_NAME_REC_PATH = "./find_name.wav";
	public static void main(String[] args) {
        // サーバーと通信する用のソケット
		MyWsClient client = null;
		try {
			CRobotPose pose = null;
			//VSMDと通信ソケット・メモリアクセス用クラス
			CRobotMem mem = new CRobotMem();
			//Sota用モーション制御クラス
			CSotaMotion motion = new CSotaMotion(mem);
			//Sota用スピーチ認識クラス
//			SpeechRecog recog = new SpeechRecog(motion);
			//sotawish初期化
			MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);

			//マイク
			CRecordMic mic = new CRecordMic();
			//< Socket設定>
	        MyWsClient.on(new MessageListener());
//	        MyWsClient.on(new AudioListener());
	        MyWsClient.on(new FindNameListener());
	        MyWsClient.on(new YesOrNoListener());
	        MyWsClient.on(new SpeechRecListener());
	        client = new MyWsClient(new URI("ws://192.168.1.49:8000"));
	        client.connect();
	        //</ Socket設定>

	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(Store.SOTA_STATE);
//			FindNameState findNameState = (FindNameState)Store.getState(Store.FIND_NAME_STATE);
			SpeechRecState speechRecState = (SpeechRecState)Store.getState(Store.SPEECH_REC_STATE);
			// </stateの取得>
			// sotaのモードを取得
			String mode = sotaState.getMode();
			if(mem.Connect()){
				//Sota仕様にVSMDを初期化
				motion.InitRobot_Sota();
				// 気を付けのポーズに戻す
				MotionSample.defaultPose(pose, mem, motion);
				// sotaのモードをlisteningに変化
				Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
				while(true){
					// モード取得
					mode = sotaState.getMode();
					if(mode == SotaState.Mode.LISTENING) {
//						boolean isListened = speechRec(pose, mem, motion, sotawish, mic, 10);
//						if(isListened) {
//							Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
//						}
						recordForSpRecByHttp(mic);
					}
					else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = speechRecState.getResult();
						if(recordResult != ""){
							sotawish.StopIdling();
//							sotawish.SayFile(TextToSpeechSota.getTTSFile(recordResult),MotionAsSotaWish.MOTION_TYPE_TALK);

							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
									sotawish.Say("終了するよ", MotionAsSotaWish.MOTION_TYPE_BYE);
								// 通信終了
								client.disconnect();
								break;
							}else if(recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) {
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
							}else {
								sotawish.Say(recordResult);
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
						}
					}else if(mode == SotaState.Mode.FIND_NAME) {
						FindName.findName(pose, mem, motion, sotawish, mic);
					}
				}
				Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.WAIT);
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
				// LED発光
				CRobotUtil.Log(TAG, "LED");
				while(true) {
					GamingLED.on(pose, mem, motion);
				}
		}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			// 通信終了
			client.disconnect();
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
	public static boolean speechRec(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic, int maxCount) {
		String mode = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getMode();
		String result = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getResult();
		int count = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getCount();
		if(mode == SpeechRecState.Mode.NON_LISTENING) {
			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.PASSIVE_LISTENING);
		}else if(mode == SpeechRecState.Mode.PASSIVE_LISTENING) {
			MyLog.info(TAG, "passive listening");
			recordForSpRec(mic);
		}
		else if(mode == SpeechRecState.Mode.ACTIVE_LISTENING) {
			MyLog.info(TAG, "active listening");
			sotawish.StartIdling();
			recordForSpRec(mic);
		}else if(mode == SpeechRecState.Mode.FINISH_LISTENING) {
			MyLog.info(TAG, "finish listening");
			sotawish.StopIdling();
			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.NON_LISTENING);
			return true;
		}
		if(count == maxCount) {
			MyLog.info(TAG, "finish listening by maxCount");
			sotawish.StopIdling();
			Store.dispatch(Store.SPEECH_REC_STATE, SpeechRecState.Action.UPDATE_MODE, SpeechRecState.Mode.NON_LISTENING);
			return true;
		}
		return false; // 終わってない

	}

	private static void recordForSpRec(CRecordMic mic) {
		try {
			// <録音>
			mic.startRecording(TEST_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			String mode = ((SpeechRecState) Store.getState(Store.SPEECH_REC_STATE)).getMode();
			if(mode == SpeechRecState.Mode.NON_LISTENING) {
				return;
			}
			// <録音した音声をサーバーに送信できる形にエンコード>
			File audioFile = new File(
					TEST_REC_PATH);
	        byte[] bytes = FileUtils.readFileToByteArray(audioFile);
	        String encoded = Base64.getEncoder().encodeToString(bytes);
	        CRobotUtil.Log(TAG, "encoded record file");
	        // </録音した音声をサーバーに送信できる形にエンコード>
	        boolean isAdditional =  false;
	        if(((SpeechRecState)Store.getState(Store.SPEECH_REC_STATE)).getCount() > 0) {
	        	isAdditional = true;
	        }
	        String payload = "{\"sound\":"
                    + encoded.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>")
                    .replace("=", "<EQUAL>").replace(",", "<COMMA>")
            + ", \"isAdditional\":" + isAdditional
            + ", \"sendTime\":" + System.currentTimeMillis()
            + "}";
	        // 送信
	        MyWsClient.emit(SpeechRecListener.CHANNEL, payload);
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
		}
	}

	public static void recordForSpRecByHttp(CRecordMic mic) {
		try {
			// <録音>
			mic.startRecording(TEST_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			String result = MyHttpCon.speechRec(TEST_REC_PATH);
			CRobotUtil.Log(TAG, result);
			JSONObject data = new JSONObject(result);
			MyLog.info(TAG,"get audio:" + data.getString("result"));
            Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_SP_REC_RESULT, data.getString("result"));
            Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);

		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_SP_REC_RESULT,"");
            Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);

		}
	}
}
