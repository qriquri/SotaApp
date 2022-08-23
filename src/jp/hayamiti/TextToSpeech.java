package jp.hayamiti;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.TextToSpeechState;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

final public class TextToSpeech {
	private static final String TAG = "TextToSpeech";
	private static final String FILE_PATH = "open_jtalk.wav";

	final public static void main(String[] args) {
		// </JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
		CRobotPose pose = null;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		//sotawish初期化
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
		//Store 初期化 stateを束ねる
		ArrayList<State> stateList = new ArrayList<State>() {{
			add(new TextToSpeechState());
		}};
		Store.bind(stateList);
		Store.dispatch(TextToSpeechState.class, TextToSpeechState.Action.SET_METHOD, TextToSpeechState.Method.OPEN_J_TALK);
		if(mem.Connect()){
			//Sota仕様にVSMDを初期化
			motion.InitRobot_Sota();
			motion.ServoOn();
			speech("こんにちは", sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
			speech("私の名前はそーたですよ。", sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
		}
	}

	/**
	 * 音声合成を行い再生する
	 * @param text
	 * @param sotawish
	 * @param scene モーションの種類
	 */
	final public static void speech(String text, MotionAsSotaWish sotawish,  String scene) {
		TextToSpeechState state = (TextToSpeechState) Store.getState(TextToSpeechState.class);
		try {
			if(state.getMethod() == TextToSpeechState.Method.SOTA_CLOUD) {
				sotawish.Say(text, scene);
			}else if(state.getMethod() == TextToSpeechState.Method.OPEN_J_TALK) {
				// openJTalkで音声合成
				byte[] response = MyHttpCon.openJTalkRec(text);
				//<ファイルに書き込む>
				BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(FILE_PATH));
				bf.write(response);
				bf.flush();
				bf.close();
				//<ファイルに書き込む>
				sotawish.SayFile(FILE_PATH, scene);
			}

		} catch (Exception e) {
			CRobotUtil.Log(TAG, e.toString());
			e.printStackTrace();
		}
	}
}
