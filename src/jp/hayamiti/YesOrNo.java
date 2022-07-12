package jp.hayamiti;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.YesOrNoRes;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class YesOrNo {
	static final String TAG = "YesOrNO";
	static final String YES_OR_NO_REC_PATH = "./yes_or_no_rec.wav";
	static final String REC_START_SOUND = "sound/mao-damasi-onepoint23.wav";
	/**
	 * 名前聞き取り
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 */
	public static void yesOrNo(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
//		String mode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
		Enum<YesOrNoState.Mode> mode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if(mode == YesOrNoState.Mode.LISTENNING_YES_OR_NO) {
			// 聞き取る
			recordARecog(mic, sotawish, motion);
		}else if(mode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			// モード更新
			Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENNING_YES_OR_NO);
		}else if(mode == YesOrNoState.Mode.ERROR) {
			// 名前を見つけられなかったとき
			TextToSpeech.speech("聞き取れなかったよ", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			// <モード更新>
			Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENNING_YES_OR_NO);
			// <モード更新>
		}
	}

//	private static void recordForYesOrNo(CRecordMic mic, MotionAsSotaWish sotawish) {
//		try {
//			// <録音>
//			mic.startRecording(YES_OR_NO_REC_PATH,3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
//			// <録音した音声をサーバーに送信できる形にエンコード>
//			File audioFile = new File(YES_OR_NO_REC_PATH);
//	        byte[] bytes = FileUtils.readFileToByteArray(audioFile);
//	        String encoded = Base64.getEncoder().encodeToString(bytes);
//	        CRobotUtil.Log(TAG, "encoded record file");
//	        // </録音した音声をサーバーに送信できる形にエンコード>
//	        // 送信
//	        MyWsClient.emit(YesOrNoListener.CHANNEL, encoded.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>")
//                    .replace("=", "<EQUAL>").replace(",", "<COMMA>"));
//		}catch(Exception e) {
//			CRobotUtil.Log(TAG, e.toString());
//		}
//	}

	private static void recordARecog(CRecordMic mic, MotionAsSotaWish sotawish, CSotaMotion motion) {
		try {
//			//音声ファイル再生
//			//raw　Waveファイルのみ対応
//			CPlayWave.PlayWave(REC_START_SOUND, false);
//			// <録音>
//			mic.startRecording(YES_OR_NO_REC_PATH,3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
			SpeechRec.speechRec(mic, motion);
			String result = MyHttpCon.yesOrNo(((SpRecState) Store.getState(SpRecState.class)).getAlternative());
			CRobotUtil.Log(TAG, result);
			YesOrNoRes res = JSONMapper.mapper.readValue(result, YesOrNoRes.class);
			String isYes =res.getResult();
			if(isYes.equals("yes")) {
        		Store.dispatch(YesOrNoState.class, YesOrNoState.Action.SET_ISYES, true);
        		Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else if (isYes.equals("no")){
        		Store.dispatch(YesOrNoState.class, YesOrNoState.Action.SET_ISYES, false);
        		Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else {
        		Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.ERROR);
        	}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			Store.dispatch(YesOrNoState.class, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.ERROR);
		}
	}
}
