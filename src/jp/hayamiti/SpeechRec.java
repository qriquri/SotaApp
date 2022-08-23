package jp.hayamiti;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.SpRecRes;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.SpeechRecog;
import jp.vstone.sotatalk.SpeechRecog.RecogResult;

final public class SpeechRec {
	static final String TAG = "SpeechRec";
	static final String TEST_REC_PATH = "./test_rec.wav";
	static final String REC_START_SOUND = "sound/mao-damasi-onepoint23.wav";
	final public static void main(String[] args) {
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
				add(new SpRecState());
			}};
			Store.bind(stateList);

	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(SotaState.class);
			SpRecState spRecState = (SpRecState)Store.getState(SpRecState.class);
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
				// 音声認識手法を設定
				Store.dispatch(SpRecState.class, SpRecState.Action.SET_METHOD, SpRecState.Method.GOOGLE);
				while(true){
					// モード取得
					mode = sotaState.getMode();
					if(mode == SotaState.Mode.LISTENING) {
						speechRec(mic, motion);
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
					}
					else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = spRecState.getResult();
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
						}else {
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

	/**
	 * 音声認識
	 * @param mic
	 */
	final public static void recordARecogByHttp(CRecordMic mic) {
		try {
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			CPlayWave.PlayWave(REC_START_SOUND,false);
			// <録音>
			mic.startRecording(TEST_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			String result = MyHttpCon.speechRec(TEST_REC_PATH);
			CRobotUtil.Log(TAG, result);
			SpRecRes res = JSONMapper.mapper.readValue(result, SpRecRes.class);
			MyLog.info(TAG,"get audio:" + res.getResult());
            Store.dispatch(SpRecState.class, SpRecState.Action.UPDATE_RESULT, res.getResult());
            Store.dispatch(SpRecState.class, SpRecState.Action.UPDATE_ALTER, res.getAlternative());

		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			Store.dispatch(SpRecState.class, SpRecState.Action.UPDATE_RESULT,"");


		}
	}

	final public static void recordARecogBySotaCloud(CSotaMotion motion) {
		SpeechRecog recog = new SpeechRecog(motion);
		RecogResult result = recog.getRecognition(20000);
		String text = "";
		if(result.recognized){
			text = result.getBasicResult();
		}
		Store.dispatch(SpRecState.class, SpRecState.Action.UPDATE_RESULT, text);
		List<String> resultList = new ArrayList<String>();
		resultList.add(text);
		Store.dispatch(SpRecState.class, SpRecState.Action.UPDATE_ALTER, resultList);
	}

	final public static boolean speechRec(CRecordMic mic, CSotaMotion motion) {
		SpRecState state = (SpRecState)Store.getState(SpRecState.class);
		switch((SpRecState.Method)state.getMethod()) {
		case GOOGLE:
			recordARecogByHttp(mic);
			break;
		case SOTA_CLOUD:
			recordARecogBySotaCloud(motion);
			break;
		}
		return true;

	}
}
