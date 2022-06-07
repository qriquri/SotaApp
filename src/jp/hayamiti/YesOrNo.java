package jp.hayamiti;

import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
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
	/**
	 * 名前聞き取り
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 */
	public static void yesOrNo(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		String mode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
		if(mode == YesOrNoState.Mode.LISTENNING_YES_OR_NO) {
			// 聞き取る
			recordForYesOrNoByHttp(mic, sotawish);
//			Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.WAIT);
		}else if(mode == YesOrNoState.Mode.WAIT) {
			// 待機 ユーザーの応答とサーバーからの応答を待つときにこの状態になる
			// MyWsClientに登録したイベントリスナーがstateを書き換えることによってこの状態から抜け出せる
			GamingLED.on(pose, mem, motion);
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			sotawish.Say("はいはい");
		}else if(mode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			// 名前が合ってるか確認
			// モード更新
			Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENNING_YES_OR_NO);
		}else if(mode == YesOrNoState.Mode.ERROR) {
			// 名前を見つけられなかったとき
			sotawish.Say("聞き取れなかったよ");
			// <モード更新>
			Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENNING_YES_OR_NO);
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

	private static void recordForYesOrNoByHttp(CRecordMic mic, MotionAsSotaWish sotawish) {
		try {
			// <録音>
			mic.startRecording(YES_OR_NO_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			String result = MyHttpCon.yesOrNo(YES_OR_NO_REC_PATH);
			JSONObject data = new JSONObject(result);
			CRobotUtil.Log(TAG, result);
			String isYes =data.getString("result");
			if(isYes.equals("yes")) {
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.SET_ISYES, true);
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else if (isYes.equals("no")){
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.SET_ISYES, false);
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.LISTENED_YES_OR_NO);
        	}else {
        		Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.ERROR);
        	}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			Store.dispatch(Store.YES_OR_NO_STATE, YesOrNoState.Action.UPDATE_MODE, YesOrNoState.Mode.ERROR);
		}
	}
}
