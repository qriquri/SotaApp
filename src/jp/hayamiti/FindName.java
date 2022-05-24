package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.websocket.MyWsClient;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;
import jp.vstone.sotatalk.TextToSpeechSota;

public class FindName {
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
//	        MyWsClient.on(new MessageListener());
//	        MyWsClient.on(new AudioListener());
//	        MyWsClient.on(new FindNameListener());
//	        MyWsClient.on(new YesOrNoListener());
//	        client = new MyWsClient(new URI("ws://192.168.1.49:8000"));
//	        client.connect();
	        //</ Socket設定>

	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(Store.SOTA_STATE);
			FindNameState findNameState = (FindNameState)Store.getState(Store.FIND_NAME_STATE);
			// </stateの取得>
			// sotaのモードを取得
			String mode = sotaState.getMode();
			// sotaと会話している人の名前を取得
			ArrayList<JSONObject> results = findNameState.getResults();
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
					// 名前取得
					results = findNameState.getResults();
					if(mode == SotaState.Mode.LISTENING) {
						if(results.size() > 0) {
							ArrayList<String> names = new ArrayList<String>();
							for(int i = 0; i < results.size(); i++) {
								names.add(results.get(i).getString("furigana"));
							}
							String nameList = nameConnection(names);
							sotawish.Say(nameList+",何か話して");
							sotawish.StartIdling();
						}
						// 録音
//						recordForSpRec(mic);
				        SpeechRec.recordForSpRecByHttp(mic);
						// モード更新
//						Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.WAIT);
					}else if(mode == SotaState.Mode.WAIT) {
						GamingLED.on(pose, mem, motion);
						if(results.size() > 0) {
							//音声ファイル再生
							//raw　Waveファイルのみ対応
							sotawish.Say("うんうん");
						}
					}
					else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = sotaState.getSpRecResult();
						if(recordResult != ""){
							sotawish.StopIdling();
//							sotawish.SayFile(TextToSpeechSota.getTTSFile(recordResult),MotionAsSotaWish.MOTION_TYPE_TALK);

							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
								if(results.size() > 0) {
									ArrayList<String> names = new ArrayList<String>();
									for(int i = 0; i < results.size(); i++) {
										names.add(results.get(i).getString("furigana"));
									}
									String nameList = nameConnection(names);
									sotawish.Say(nameList + ",さようなら", MotionAsSotaWish.MOTION_TYPE_BYE);
									final int nameNum = names.size();
									// 名前削除
									for(int i = 0; i < nameNum; i++) {
										// リストは消すと減っていくから、先頭を常に消す
										Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.REMOVE_NAME, 0);
									}
									MyLog.info(TAG, "名前の数"+ findNameState.getResults());
								}else {
									sotawish.Say("終了するよ", MotionAsSotaWish.MOTION_TYPE_BYE);
								}
								// 通信終了
//								client.disconnect();
								break;
							}else if(recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) {
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
							}else {
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
						}
					}else if(mode == SotaState.Mode.FIND_NAME) {
						findName(pose, mem, motion, sotawish, mic);
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
//			client.disconnect();
		}
	}

	/**
	 * 名前聞き取り
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 */
	public static void findName(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		String mode = ((FindNameState) Store.getState(Store.FIND_NAME_STATE)).getMode();
		ArrayList<JSONObject> results = ((FindNameState) Store.getState(Store.FIND_NAME_STATE)).getResults();
		JSONArray listenResults = ((FindNameState)Store.getState(Store.FIND_NAME_STATE)).getListenResults();
		int count = ((FindNameState)Store.getState(Store.FIND_NAME_STATE)).getCount();

		if(mode == FindNameState.Mode.LISTENNING_NAME) {
			// 送信処理に時間がかかるかもしれないから、新たにスレッドを作る
			recordForFindNameByHttp(mic, sotawish);
//			Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_FIND_NAME);
		}else if(mode == FindNameState.Mode.WAIT_FIND_NAME) {
			// 待機 ユーザーの応答とサーバーからの応答を待つときにこの状態になる
			// MyWsClientに登録したイベントリスナーがstateを書き換えることによってこの状態から抜け出せる
			GamingLED.on(pose, mem, motion);
			//音声ファイル再生
			//raw Waveファイルのみ対応
			sotawish.Say("なるほどなるほど");
		}else if(mode == FindNameState.Mode.CONFORM_NAME) {
			// 名前が合ってるか確認
			CRobotUtil.Log(TAG,"データベースに登録されていた数" + (listenResults.length()));
			MyLog.info(TAG, "count = "+count);
			String newName = "";
			if(listenResults.getJSONObject(count).getBoolean("isRegistered")) {
				// 登録済みならニックネームで呼ぶ
				newName = listenResults.getJSONObject(count).getString("nickName");
			}else {
				// 未登録な名前で呼ぶ
				newName = listenResults.getJSONObject(count).getString("furigana");
			}
			sotawish.Say(newName +"さん,で合ってる?");
			// モード更新
			if(listenResults.length() == 1) {
				Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_CONFORM);
			}else if(listenResults.length() > 1) {
				// 結果が複数の場合
				Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_CONFORM_MULTIPLE);
			}
		}else if(mode == FindNameState.Mode.WAIT_CONFORM) {
			// 確認待機
			String yesOrNoMode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
			if(yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
				boolean isYes = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getIsYes();
				if(isYes) {
					// モード更新
					Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.FINDED_NAME);
					Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.ADD_NAME, listenResults.getJSONObject(count));
				}else {
					// 聞き直す
					// モード更新
					Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
				}
			}else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
				// 確認しなおす
				// モード更新
				Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
			}
			// yesOrNo処理
			YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
		}else if(mode == FindNameState.Mode.WAIT_CONFORM_MULTIPLE){
			// 確認待機
			String yesOrNoMode = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getMode();
			if(yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
				boolean isYes = ((YesOrNoState) Store.getState(Store.YES_OR_NO_STATE)).getIsYes();
				if(isYes) {
					// 正解
					// モード更新
					Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.FINDED_NAME);
					Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.ADD_NAME, listenResults.getJSONObject(count));
				}else {
					// 不正解
					if(count != listenResults.length() -1) {
						// カウントを進め、次の名前を聞くようにする
						Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.COUNT, 1);
						// モード更新
						Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
					}else {
						// 配列の最後まで聞いたら
						// 聞き直す
						// モード更新
						Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
					}
				}
			}else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
				// 確認しなおす
				// モード更新
				Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
			}
			// yesOrNo処理
			YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);

		}else if(mode == FindNameState.Mode.FINDED_NAME) {
			// 名前を見つけた時
			CRobotUtil.Log(TAG,"数" + (results.size()));
			String newName = results.get(results.size()-1).getString("furigana");
			boolean isRegistered = results.get(results.size()-1).getBoolean("isRegistered");
			if(isRegistered) {
				// すでに記憶済みの名前の時
				CRobotUtil.Log(TAG, newName);
				sotawish.Say(newName+"さん,こんにちは");
			}else {
				// 初めての名前の時
				CRobotUtil.Log(TAG, newName);
				sotawish.Say(newName+"さん,初めまして.まだデータベースに登録されてないから、後で登録してね.");
			}
			// <モード更新>
			Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
			// また呼ばれたときのために始めのモードに戻しておく
			Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
			// </モード更新>
		}else if(mode == FindNameState.Mode.ERROR_NAME) {
			// 名前を見つけられなかったとき
			sotawish.Say("聞き取れなかったよ");
			// <モード更新>
			// もう一度やり直す
			Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
			// <モード更新>
		}
	}

//	private static void recordForSpRec(CRecordMic mic) {
//		try {
//			// <録音>
//			mic.startRecording(TEST_REC_PATH,3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
//			// <録音した音声をサーバーに送信できる形にエンコード>
//			File audioFile = new File(
//					TEST_REC_PATH);
//	        byte[] bytes = FileUtils.readFileToByteArray(audioFile);
//	        String encoded = Base64.getEncoder().encodeToString(bytes);
//	        CRobotUtil.Log(TAG, "encoded record file");
//	        // </録音した音声をサーバーに送信できる形にエンコード>
//	        // 送信
//	        MyWsClient.emit(AudioListener.CHANNEL, encoded.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>")
//                    .replace("=", "<EQUAL>").replace(",", "<COMMA>"));
//		}catch(Exception e) {
//			CRobotUtil.Log(TAG, e.toString());
//		}
//	}

//	private static void recordForFindName(CRecordMic mic, MotionAsSotaWish sotawish) {
//		try {
//			sotawish.SayFile(TextToSpeechSota.getTTSFile("あなたの名前は？"),MotionAsSotaWish.MOTION_TYPE_CALL);
//
//			// <録音>
//			mic.startRecording(FIND_NAME_REC_PATH,3000);
//			mic.waitend();
//			CRobotUtil.Log(TAG, "wait end");
//			// </録音>
//			// <録音した音声をサーバーに送信できる形にエンコード>
//			File audioFile = new File(
//					FIND_NAME_REC_PATH);
//	        byte[] bytes = FileUtils.readFileToByteArray(audioFile);
//	        String encoded = Base64.getEncoder().encodeToString(bytes);
//	        CRobotUtil.Log(TAG, "encoded record file");
//	        // </録音した音声をサーバーに送信できる形にエンコード>
//	        // 送信
//	        MyWsClient.emit(FindNameListener.CHANNEL, encoded.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>")
//                    .replace("=", "<EQUAL>").replace(",", "<COMMA>"));
//		}catch(Exception e) {
//			CRobotUtil.Log(TAG, e.toString());
//		}
//	}

	private static void recordForFindNameByHttp(CRecordMic mic, MotionAsSotaWish sotawish) {
		try {
			sotawish.SayFile(TextToSpeechSota.getTTSFile("あなたの名前は？"),MotionAsSotaWish.MOTION_TYPE_CALL);

			// <録音>
			mic.startRecording(FIND_NAME_REC_PATH,3000);
			mic.waitend();
			CRobotUtil.Log(TAG, "wait end");
			// </録音>
			//<名前認識>
			String result = MyHttpCon.nameRec(FIND_NAME_REC_PATH , MyHttpCon.API_HOME + "/nameRec" + "?sendTime=" + System.currentTimeMillis());
			CRobotUtil.Log(TAG, result);
			JSONObject data = new JSONObject(result);
			String nameKana = data.getString("result");
			CRobotUtil.Log(TAG, nameKana);

			//</名前認識>
			//<データベースからユーザー情報を取得>
			result = MyHttpCon.getUserNames(MyHttpCon.DB_HOME + "/getUserNames", nameKana);
			CRobotUtil.Log(TAG, result);
			JSONObject userNames = new JSONObject(result);
			Boolean err = userNames.getBoolean("err");
			if(err) {
				Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
			}else {

	    		// 追加する
	        	Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.SET_LISTEN_RESULT, userNames.getJSONArray("users"));
	            Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
			}
			//</データベースからユーザー情報を取得>
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
            Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
		}
	}

	private static String nameConnection(ArrayList<String> names) {
		String nameList = "";
		for(int i =0; i < names.size(); i++) {
			nameList += names.get(i).split(",")[0] + "さん,";
		}
		return nameList;
	}

}
