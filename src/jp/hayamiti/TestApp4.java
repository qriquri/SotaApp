package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.BasicRes;
import jp.hayamiti.httpCon.ApiCom.GenerateSentenceRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.DayQsState;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.GenerateSentenceState;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.StoreTest4;
import jp.hayamiti.state.TextToSpeechState;
import jp.hayamiti.utils.MyLog;
import jp.hayamiti.utils.MyStrBuilder;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class TestApp4 {
	static final String TAG = "TestApp4";
	static final String START_SOUND = "sound/mao-damasi-system04.wav";

	public static void main(String[] args) {
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
		//sotawish初期化
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
		//マイク
		CRecordMic mic = new CRecordMic();
		try {
			//Store 初期化 stateを束ねる
			StoreTest4.setup();
			// <stateの取得>
			SotaState sotaState = (SotaState) Store.getState(SotaState.class);
			SpRecState spRecState = (SpRecState) Store.getState(SpRecState.class);
			FindNameState findNameState = (FindNameState) Store.getState(FindNameState.class);
			DayQsState dayQsState = (DayQsState) Store.getState(DayQsState.class);
			// </stateの取得>
			// sotaのモードを取得
			Enum<SotaState.Mode> mode = sotaState.getMode();
			// sotaと会話している人の名前を取得
			ArrayList<User> fnResults = findNameState.getResults();
			// 音声合成手法を設定
			Store.dispatch(TextToSpeechState.class, TextToSpeechState.Action.SET_METHOD,
					TextToSpeechState.Method.SOTA_CLOUD);
			// 音声認識手法を設定
			Store.dispatch(SpRecState.class, SpRecState.Action.SET_METHOD, SpRecState.Method.SOTA_CLOUD);

			if (mem.Connect()) {
				//Sota仕様にVSMDを初期化
				motion.InitRobot_Sota();
				motion.ServoOn();
				GamingLED.off(pose, mem, motion);
				// 気を付けのポーズに戻す
				MotionSample.defaultPose(pose, mem, motion);
				//音声ファイル再生
				//raw　Waveファイルのみ対応
				CPlayWave.PlayWave(START_SOUND, false);
				GamingLED.on(pose, mem, motion);
				// sotaのモードをlisteningに変化
				Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
				CRobotUtil.wait(1000);
				// sotaに待機モーションをさせる
				sotawish.StartIdling();
				while (true) {
					// モード取得
					mode = sotaState.getMode();
					if (mode == SotaState.Mode.LISTENING) {
						// <話しかけられるのを待つ>
						// sotaに待機モーションをさせる
						sotawish.StartIdling();
						// 録音
						SpeechRec.speechRec(mic, motion);
						// モード更新
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
						// <話しかけられるのを待つ>
					} else if (mode == SotaState.Mode.JUDDGING) {
						// <LISTENINGモードで聞き取った音声の判定>
						boolean isFinish = juddging(sotawish, spRecState, findNameState, fnResults);
						if (isFinish) {
							break;
						}
						;
						// </LISTENINGモードで聞き取った音声の判定>
					} else if (mode == SotaState.Mode.FIND_NAME) {
						// <名前聞き取り>
						boolean isFind = FindName.findName(pose, mem, motion, sotawish, mic);
						if (isFind) {
							if (findNameState.getResults().get(0).getIsRegistered()) {
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
										SotaState.Mode.LISTEN_BACK_DAY);
							} else {
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);

							}
						}
						// </名前聞き取り>
					} else if (mode == SotaState.Mode.LISTEN_BACK_DAY) {
						// 登録する日にちをきく
						if (DayQs.dayQs(pose, mem, motion, sotawish, mic)) {
							if (dayQsState.getIsEnd()) {
								// 終了すると答えた場合
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.ADVISE);
							} else {
								// 日にちを答えた場合
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
										SotaState.Mode.CONFORM_ALEADY_LISTENED);
							}
						}
					} else if (mode == SotaState.Mode.CONFORM_ALEADY_LISTENED) {
						// <すでに質問済みかを確認する>
						String nickName = fnResults.get(fnResults.size() - 1).getNickName();
						int backDay = dayQsState.getResult();
						if (nickName.equals("")) {
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
						} else {
							String res = MyHttpCon.getHabits(nickName, true, backDay, backDay);
							JSONObject data = new JSONObject(res);
							Boolean success = data.getBoolean("success");
							String relativeToday = backDay == 0 ? "今日" : MyStrBuilder.build(5, backDay, "日前");
							if (success) {
								// 質問は一日一回
								TextToSpeech.speech(MyStrBuilder.build(64, relativeToday, "はもう聞いたみたいだよ"), sotawish,
										MotionAsSotaWish.MOTION_TYPE_TALK);
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
										SotaState.Mode.LISTEN_BACK_DAY);
							} else {
								// まだ質問してない場合、質問する
								TextToSpeech.speech(MyStrBuilder.build(64, relativeToday, "はまだ聞いてないみたいだから、いくつか質問するよ"),
										sotawish, MotionAsSotaWish.MOTION_TYPE_TALK);
								// 質問結果をリセット
								Store.dispatch(HabitQsState.class, HabitQsState.Action.RESET_RESULT, null);
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
										SotaState.Mode.LISTEN_HABIT);
							}
						}
						// </すでに質問済みかを確認する>
					} else if (mode == SotaState.Mode.LISTEN_HABIT) {
						// <生活習慣を聞き出す>
						//						String nickName = results.get(results.size()-1).getString("nickName");
						int backDay = dayQsState.getResult();
						if (HabitQs.habitQs(pose, mem, motion, sotawish, mic, backDay)) {
							// 質問が終わったら
							// モード更新
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
									SotaState.Mode.LISTEN_CONDITION);
						}
						// </生活習慣を聞き出す>
					} else if (mode == SotaState.Mode.LISTEN_CONDITION) {
						// 体調を聞き出す
						int backDay = dayQsState.getResult();
						if (ConditionQs.conditionQs(pose, mem, motion, sotawish, mic, backDay)) {
						    HabitQs.sendResult(backDay);
						    ConditionQs.sendResult(backDay);
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE,
									SotaState.Mode.LISTEN_BACK_DAY);
						}
					} else if (mode == SotaState.Mode.ADVISE) {
						// 次の週の改善目標を提案する
						SuggestNextHabit.suggestNextHabit(pose, mem, motion, sotawish, mic);
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
					} else if (mode == SotaState.Mode.FIN) {
					    if(GenerateSentence.generateSentence("早寝早起きのメリットは")) {
		                    final GenerateSentenceRes res = ((GenerateSentenceState)Store.getState(GenerateSentenceState.class)).getResult();
		                    String result = res.getResult().get((int)(res.getResult().size() * Math.random()));
		                    TextToSpeech.speech(result, sotawish, result);
		                }
						// 聞き取り終了
						TextToSpeech.speech("質問はこれで終わり。", sotawish, MotionAsSotaWish.MOTION_TYPE_LOW);
						String nameList = fnResults.get(0).getFurigana();
						// </sotaが認識した名前を繋げる>
						TextToSpeech.speech(MyStrBuilder.build(64, nameList, "さん,さようなら"), sotawish,
								MotionAsSotaWish.MOTION_TYPE_BYE);
						// 名前削除
						// リストは消すと減っていくから、先頭を常に消す
						Store.dispatch(FindNameState.class, FindNameState.Action.REMOVE_NAME, 0);
						MyLog.info(TAG, MyStrBuilder.build(32, "名前の数", findNameState.getResults()));
						// 初めの状態に戻る
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
					}
				}
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
			}
		} catch (Exception e) {
			CRobotUtil.Log(TAG, e.toString());
		} finally {
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			CPlayWave.PlayWave(START_SOUND, false);
			GamingLED.off(pose, mem, motion);
			//サーボモータのトルクオフ
			motion.ServoOff();
		}
	}

	private static boolean juddging(MotionAsSotaWish sotawish, SpRecState spRecState, FindNameState findNameState,
			ArrayList<User> fnResults) {
		String recordResult = spRecState.getResult();
		if (recordResult != "") {
			sotawish.StopIdling();
			// <聞き取った内容に応じて処理する>
			if (recordResult.contains("おわり") || recordResult.contains("終わり")) {
				if (fnResults.size() > 0) {
					ArrayList<String> names = new ArrayList<String>();
					for (int i = 0; i < fnResults.size(); i++) {
						names.add(fnResults.get(i).getFurigana());
					}
					String nameList = FindName.nameConnection(names);
					TextToSpeech.speech(MyStrBuilder.build(63, nameList, ",さようなら"), sotawish,
							MotionAsSotaWish.MOTION_TYPE_BYE);
					final int nameNum = names.size();
					// 名前削除
					for (int i = 0; i < nameNum; i++) {
						// リストは消すと減っていくから、先頭を常に消す
						Store.dispatch(FindNameState.class, FindNameState.Action.REMOVE_NAME, 0);
					}
					MyLog.info(TAG, MyStrBuilder.build(64, "名前の数", findNameState.getResults()));
				} else {
					TextToSpeech.speech("終了するよ", sotawish, MotionAsSotaWish.MOTION_TYPE_BYE);
				}
				return true;
			} else if ((recordResult.contains("おはよう") || recordResult.contains("こんにちは")
					|| recordResult.contains("こんばんは")) && fnResults.size() == 0) {
				// モード更新
				Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
			} else {
				// モード更新
				Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
			}
			// </聞き取った内容に応じて処理する>
		} else {
			// モード更新
			Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
		}
		return false;
	}
}
