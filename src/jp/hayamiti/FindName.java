package jp.hayamiti;

import java.util.ArrayList;

import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.NameRecRes;
import jp.hayamiti.httpCon.DbCom.GetUserNamesRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.FindNameState;
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

final public class FindName {
	private static final String TAG = "FindNmae";
	final public static void main(String[] args) {
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
				add(new SpRecState());
				add(new FindNameState());
				add(new YesOrNoState());
			}};
			Store.bind(stateList);

	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(SotaState.class);
			SpRecState spRecState = (SpRecState)Store.getState(SpRecState.class);
			FindNameState findNameState = (FindNameState)Store.getState(FindNameState.class);
			// </stateの取得>
			// sotaのモードを取得
			Enum<SotaState.Mode> mode = sotaState.getMode();
			// sotaと会話している人の名前を取得
			ArrayList<User> results = findNameState.getResults();
			if(mem.Connect()){
				//Sota仕様にVSMDを初期化
				motion.InitRobot_Sota();
				// サーボモーターをon
				motion.ServoOn();
				// 気を付けのポーズに戻す
				MotionSample.defaultPose(pose, mem, motion);
				// sotaのモードをlisteningに変化
				Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);

				Store.dispatch(SpRecState.class, SpRecState.Action.SET_METHOD, SpRecState.Method.GOOGLE);
				while(true){
					// モード取得
					mode = sotaState.getMode();
					// 名前取得
					results = findNameState.getResults();
					if(mode == SotaState.Mode.LISTENING) {
						if(results.size() > 0) {
							ArrayList<String> names = new ArrayList<String>();
							for(int i = 0; i < results.size(); i++) {
								names.add(results.get(i).getFurigana());
							}
							String nameList = nameConnection(names);
							TextToSpeech.speech(nameList+",何か話して", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
							sotawish.StartIdling();
						}
						// 録音
//						recordForSpRec(mic);
				        SpeechRec.speechRec(mic, motion);
						// モード更新
				        Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
					}else if(mode == SotaState.Mode.WAIT) {
						GamingLED.on(pose, mem, motion);
						if(results.size() > 0) {
							//音声ファイル再生
							//raw　Waveファイルのみ対応
							TextToSpeech.speech("うんうん", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
						}
					}
					else if(mode == SotaState.Mode.JUDDGING){
						String recordResult = spRecState.getResult();
						if(recordResult != ""){
							sotawish.StopIdling();
//							TextToSpeech.speechFile(TextToSpeechSota.getTTSFile(recordResult),MotionAsSotaWish.MOTION_TYPE_TALK);

							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
								if(results.size() > 0) {
									ArrayList<String> names = new ArrayList<String>();
									for(int i = 0; i < results.size(); i++) {
										names.add(results.get(i).getFurigana());
									}
									String nameList = nameConnection(names);
									TextToSpeech.speech(nameList + ",さようなら", sotawish, MotionAsSotaWish.MOTION_TYPE_BYE);
									final int nameNum = names.size();
									// 名前削除
									for(int i = 0; i < nameNum; i++) {
										// リストは消すと減っていくから、先頭を常に消す
										Store.dispatch(FindNameState.class, FindNameState.Action.REMOVE_NAME, 0);
									}
									MyLog.info(TAG, "名前の数"+ findNameState.getResults());
								}else {
									TextToSpeech.speech("終了するよ", sotawish, MotionAsSotaWish.MOTION_TYPE_BYE);
								}
								// 通信終了
//								client.disconnect();
								break;
							}else if(recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
							}else {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
						}else {
							// モード更新
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
						}
					}else if(mode == SotaState.Mode.FIND_NAME) {
						if(findName(pose, mem, motion, sotawish, mic)) {
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
	 * 名前聞き取り
	 * @param pose
	 * @param mem
	 * @param motion
	 * @param sotawish
	 * @param mic
	 */
	final public static boolean findName(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic) {
		Enum<FindNameState.Mode> mode = ((FindNameState) Store.getState(FindNameState.class)).getMode();
		ArrayList<User> results = ((FindNameState) Store.getState(FindNameState.class)).getResults();
		ArrayList<User> listenResults = ((FindNameState)Store.getState(FindNameState.class)).getListenResults();
		int count = ((FindNameState)Store.getState(FindNameState.class)).getCount();
		boolean isFind = false;
		if(mode == FindNameState.Mode.LISTENNING_NAME) {
			// 聞き取り
			recordARec(mic, sotawish, motion);
		}else if(mode == FindNameState.Mode.CONFORM_NAME) {
			// 名前が合ってるか確認
			conformName(sotawish, count, listenResults);
		}else if(mode == FindNameState.Mode.WAIT_CONFORM) {
			// 確認待機
			waitConform(pose, mem, motion, sotawish, mic, count, listenResults);
		}else if(mode == FindNameState.Mode.WAIT_CONFORM_MULTIPLE){
			// 確認待機
			 waitConfromMultiple(pose, mem, motion, sotawish, mic, count, listenResults);
		}else if(mode == FindNameState.Mode.FINDED_NAME) {
			// 名前を見つけた時
			isFind = findedName(sotawish, results);
		}else if(mode == FindNameState.Mode.ERROR_NAME) {
			// 名前を見つけられなかったとき
			TextToSpeech.speech("聞き取れなかったよ", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
			// <モード更新>
			// もう一度やり直す
			Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
			// <モード更新>
		}
		return isFind;
	}

	final private static void recordARec(CRecordMic mic, MotionAsSotaWish sotawish, CSotaMotion motion) {
		try {
			TextToSpeech.speech("あなたの名前は？",sotawish, MotionAsSotaWish.MOTION_TYPE_CALL);
			// </録音>
			SpeechRec.speechRec(mic, motion);
			//<名前認識>
			NameRecRes res = MyHttpCon.nameRec(((SpRecState) Store.getState(SpRecState.class)).getResult());
            CRobotUtil.Log(TAG, res.toString());
			String nameKana = res.getResult();
			CRobotUtil.Log(TAG, nameKana);

			//</名前認識>
			//<データベースからユーザー情報を取得>
			GetUserNamesRes res2 = MyHttpCon.getUserNames(nameKana);
			CRobotUtil.Log(TAG, res2.toString());
			Boolean err = res2.isErr();
			if(err) {
				Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
			}else {

	    		// 追加する
	        	Store.dispatch(FindNameState.class, FindNameState.Action.SET_LISTEN_RESULT, res2.getUsers());
	            Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
			}
			//</データベースからユーザー情報を取得>
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
            Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.ERROR_NAME);
		}
	}

	final private static void conformName(MotionAsSotaWish sotawish, int count, ArrayList<User> listenResults) {
		CRobotUtil.Log(TAG,MyStrBuilder.build(64, "データベースに登録されていた数", listenResults.size()));
		MyLog.info(TAG, "count = "+count);
		String newName = "";
		if(listenResults.get(count).getIsRegistered()) {
			// 登録済みならニックネームで呼ぶ
			newName = listenResults.get(count).getNickName();
		}else {
			// 未登録な名前で呼ぶ
			newName = listenResults.get(count).getFurigana();
		}
		TextToSpeech.speech(MyStrBuilder.build(64, newName, "さん,で合ってる?"), sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
		// モード更新
		if(listenResults.size() == 1) {
			Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_CONFORM);
		}else if(listenResults.size() > 1) {
			// 結果が複数の場合
			Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.WAIT_CONFORM_MULTIPLE);
		}
	}

	final private static void waitConform(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic ,int count, ArrayList<User> listenResults) {
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if(yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if(isYes) {
				// モード更新
				Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.FINDED_NAME);
				Store.dispatch(FindNameState.class, FindNameState.Action.ADD_NAME, listenResults.get(count));
			}else {
				// 聞き直す
				// モード更新
				Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
			}
		}else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
			// 確認しなおす
			// モード更新
			Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
		}
		// yesOrNo処理
		YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);
	}

	final private static void waitConfromMultiple(CRobotPose pose, CRobotMem mem, CSotaMotion motion, MotionAsSotaWish sotawish, CRecordMic mic ,int count, ArrayList<User> listenResults) {
		Enum<YesOrNoState.Mode> yesOrNoMode = ((YesOrNoState) Store.getState(YesOrNoState.class)).getMode();
		if(yesOrNoMode == YesOrNoState.Mode.LISTENED_YES_OR_NO) {
			boolean isYes = ((YesOrNoState) Store.getState(YesOrNoState.class)).getIsYes();
			if(isYes) {
				// 正解
				// モード更新
				Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.FINDED_NAME);
				Store.dispatch(FindNameState.class, FindNameState.Action.ADD_NAME, listenResults.get(count));
			}else {
				// 不正解
				if(count != listenResults.size() -1) {
					// カウントを進め、次の名前を聞くようにする
					Store.dispatch(FindNameState.class, FindNameState.Action.COUNT, 1);
					// モード更新
					Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
				}else {
					// 配列の最後まで聞いたら
					// 聞き直す
					// モード更新
					Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
				}
			}
		}else if (yesOrNoMode == YesOrNoState.Mode.ERROR) {
			// 確認しなおす
			// モード更新
			Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.CONFORM_NAME);
		}
		// yesOrNo処理
		YesOrNo.yesOrNo(pose, mem, motion, sotawish, mic);

	}

	final private static boolean findedName(MotionAsSotaWish sotawish, ArrayList<User> results) {
		CRobotUtil.Log(TAG,"数" + (results.size()));
		String newName = results.get(results.size()-1).getFurigana();
		boolean isRegistered = results.get(results.size()-1).getIsRegistered();
		if(isRegistered) {
			// すでに記憶済みの名前の時
			CRobotUtil.Log(TAG, newName);
			TextToSpeech.speech(MyStrBuilder.build(64, newName,"さん,こんにちは"), sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
		}else {
			// 初めての名前の時
			CRobotUtil.Log(TAG, newName);
			TextToSpeech.speech(MyStrBuilder.build(64, newName,"さん,初めまして.まだデータベースに登録されてないから、後で登録してね."), sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
		}
		// <モード更新>
		// また呼ばれたときのために始めのモードに戻しておく
		Store.dispatch(FindNameState.class, FindNameState.Action.UPDATE_MODE, FindNameState.Mode.LISTENNING_NAME);
		// </モード更新>
		return true;
	}

	final public static String nameConnection(ArrayList<String> names) {
		String nameList = "";
		for(int i =0; i < names.size(); i++) {
			nameList += names.get(i).split(",")[0] + "さん,";
		}
		return nameList;
	}

}
