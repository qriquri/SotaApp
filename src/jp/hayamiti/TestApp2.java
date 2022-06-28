package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.BasicRes;
import jp.hayamiti.httpCon.DbCom.User;
import jp.hayamiti.state.ConditionQsState;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.YesOrNoState;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class TestApp2 {
	static final String TAG = "TestApp2";
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
			ArrayList<State> stateList = new ArrayList<State>() {{
				add(new SotaState());
				add(new FindNameState());
				add(new YesOrNoState());
				add(new HabitQsState());
				add(new ConditionQsState());
			}};
			Store.bind(stateList);
	        // <stateの取得>
			SotaState sotaState = (SotaState)Store.getState(SotaState.class);
			FindNameState findNameState = (FindNameState)Store.getState(FindNameState.class);
			// </stateの取得>
			// sotaのモードを取得
			Enum<SotaState.Mode> mode = sotaState.getMode();
			// sotaと会話している人の名前を取得
			ArrayList<User> results = findNameState.getResults();
			if(mem.Connect()){
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
				// sotaに待機モーションをさせる
				sotawish.StartIdling();
				while(true){
					// モード取得
					mode = sotaState.getMode();
					if(mode == SotaState.Mode.LISTENING) {
						// <話しかけられるのを待つ>
						// sotaに待機モーションをさせる
						sotawish.StartIdling();
						// 録音
				        SpeechRec.recordARecogByHttp(mic);
						// モード更新
				        Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
				        // <話しかけられるのを待つ>
					}else if(mode == SotaState.Mode.JUDDGING){
						// <LISTENINGモードで聞き取った音声の判定>
						String recordResult = sotaState.getSpRecResult();
						if(recordResult != ""){
							sotawish.StopIdling();
							// <聞き取った内容に応じて処理する>
							if(recordResult.contains("おわり") || recordResult.contains("終わり")){
								if(results.size() > 0) {
									ArrayList<String> names = new ArrayList<String>();
									for(int i = 0; i < results.size(); i++) {
										names.add(results.get(i).getFurigana());
									}
									String nameList = FindName.nameConnection(names);
									sotawish.Say(nameList + ",さようなら", MotionAsSotaWish.MOTION_TYPE_BYE);
									final int nameNum = names.size();
									// 名前削除
									for(int i = 0; i < nameNum; i++) {
										// リストは消すと減っていくから、先頭を常に消す
										Store.dispatch(FindNameState.class, FindNameState.Action.REMOVE_NAME, 0);
									}
									MyLog.info(TAG, "名前の数"+ findNameState.getResults());
								}else {
									sotawish.Say("終了するよ", MotionAsSotaWish.MOTION_TYPE_BYE);
								}
								break;
							}else if((recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) && results.size() == 0) {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
							}else {
								// モード更新
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
							// </聞き取った内容に応じて処理する>
						}else {
							// モード更新
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
						}
						// </LISTENINGモードで聞き取った音声の判定>
					}else if(mode == SotaState.Mode.FIND_NAME) {
						// <名前聞き取り>
						boolean isFind = FindName.findName(pose, mem, motion, sotawish, mic);
						if(isFind) {
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.CONFORM_ALEADY_LISTENED);
						}
						// </名前聞き取り>
					}else if(mode == SotaState.Mode.CONFORM_ALEADY_LISTENED) {
						// <すでに質問済みかを確認する>
						String nickName = results.get(results.size()-1).getNickName();
						if(nickName.equals("")) {
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
						}else {
							String res = MyHttpCon.getTodayHabit(nickName, true);
							JSONObject data = new JSONObject(res);
							Boolean success = data.getBoolean("success");
							if(success) {
								// 質問は一日一回
								sotawish.Say("今日はもう聞いたみたいだから、終了するよ");
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
							}else {
								// まだ質問してない場合、質問する
								sotawish.Say("今日はまだ聞いてないみたいだから、いくつか質問するよ");
								// 質問結果をリセット
								Store.dispatch(HabitQsState.class, HabitQsState.Action.RESET_RESULT, null);
								Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTEN_HABIT);
							}
						}
						// </すでに質問済みかを確認する>
					}else if(mode == SotaState.Mode.LISTEN_HABIT) {
						// <生活習慣を聞き出す>
//						String nickName = results.get(results.size()-1).getString("nickName");
						if( HabitQs.habitQs(pose, mem, motion, sotawish, mic)) {
							// 質問が終わったら
						 	// モード更新
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTEN_CONDITION);
				        }
						// </生活習慣を聞き出す>
					}else if(mode == SotaState.Mode.LISTEN_CONDITION) {
						// 体調を聞き出す
						if(ConditionQs.conditionQs(pose, mem, motion, sotawish, mic)) {
							Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
						}
					}else if(mode == SotaState.Mode.FIN) {
						// 聞き取り終了
						sotawish.Say("今日の質問はこれで終わり。");
						String nameList = results.get(0).getFurigana();
						// </sotaが認識した名前を繋げる>
						sotawish.Say(nameList + "さん,さようなら", MotionAsSotaWish.MOTION_TYPE_BYE);
						// 名前削除
						// リストは消すと減っていくから、先頭を常に消す
						Store.dispatch(FindNameState.class, FindNameState.Action.REMOVE_NAME, 0);
						MyLog.info(TAG, "名前の数"+ findNameState.getResults());
						// 初めの状態に戻る
						Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
					}
				}
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
		}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
		}finally {
			//音声ファイル再生
			//raw　Waveファイルのみ対応
			CPlayWave.PlayWave(START_SOUND, false);
			GamingLED.off(pose, mem, motion);
			 //サーボモータのトルクオフ
			  motion.ServoOff();
		}
	}
}
