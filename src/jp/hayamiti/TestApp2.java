package jp.hayamiti;

import java.util.ArrayList;

import org.json.JSONObject;

import jp.hayamiti.httpCon.LifeHabit;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.state.FindNameState;
import jp.hayamiti.state.HabitQsState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.Store;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class TestApp2 {
	static final String TAG = "TestApp2";
	public static void main(String[] args) {
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
				        SpeechRec.recordForSpRecByHttp(mic);
						// モード更新
				        Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.JUDDGING);
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
										names.add(results.get(i).getString("furigana"));
									}
									String nameList = FindName.nameConnection(names);
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
								break;
							}else if((recordResult.contains("おはよう") || recordResult.contains("こんにちは") || recordResult.contains("こんばんは")) && results.size() == 0) {
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIND_NAME);
							}else {
								// モード更新
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
							}
							// </聞き取った内容に応じて処理する>
						}
						// </LISTENINGモードで聞き取った音声の判定>
					}else if(mode == SotaState.Mode.FIND_NAME) {
						// <名前聞き取り>
						boolean isFind = FindName.findName(pose, mem, motion, sotawish, mic);
						if(isFind) {
							Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.CONFORM_ALEADY_LISTENED);
						}
						// </名前聞き取り>
					}else if(mode == SotaState.Mode.CONFORM_ALEADY_LISTENED) {
						// <すでに質問済みかを確認する>
						String nickName = results.get(results.size()-1).getString("nickName");
						if(nickName.equals("")) {
							Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
						}else {
							String res = MyHttpCon.getTodayHabit(nickName, true);
							JSONObject data = new JSONObject(res);
							Boolean success = data.getBoolean("success");
							if(success) {
								// 質問は一日一回
								sotawish.Say("今日はもう聞いたみたいだから、終了するよ");
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
							}else {
								// まだ質問してない場合、質問する
								sotawish.Say("今日はまだ聞いてないみたいだから、いくつか質問するよ");
								// 質問結果をリセット
								Store.dispatch(Store.HABIT_QS_STATE, HabitQsState.Action.RESET_RESULT, null);
								Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTEN_HABIT);
							}
						}
						// </すでに質問済みかを確認する>
					}else if(mode == SotaState.Mode.LISTEN_HABIT) {
						// <生活習慣を聞き出す>
						String nickName = results.get(results.size()-1).getString("nickName");
						if( HabitQs.habitQs(pose, mem, motion, sotawish, mic)) {
				        // 質問が終わったら
						// <結果を送信>
				        	HabitQsState state = (HabitQsState) Store.getState(Store.HABIT_QS_STATE);
				    		ArrayList<JSONObject> result = state.getResult();
				        	LifeHabit lifeHabit = new LifeHabit();
					        int sleepTime = Integer.parseInt(result.get(HabitQsState.SLEEP).getString("result"));
					        int getUpTime = Integer.parseInt(result.get(HabitQsState.GETUP).getString("result"));
					        lifeHabit.setVal(
					        		sleepTime,
					        		getUpTime,
					        		result.get(HabitQsState.IS_EXERCISE).getString("result").equals("yes"),
					        		result.get(HabitQsState.IS_DRINKING).getString("result").equals("yes"),
					        		result.get(HabitQsState.EAT_BREAKFAST).getString("result").equals("yes"),
					        		result.get(HabitQsState.EAT_SNACK).getString("result").equals("yes"),
					        		result.get(HabitQsState.SNACK_NAME).getString("result"));
//						        lifeHabit.setText("4時に寝た", "10時に起きた", "運動してない", "飲んだ", "食べてない", "食べた", "ポテトチップスとチョコレート食べた");
					        lifeHabit.setText(
					        		result.get(HabitQsState.SLEEP).getString("text"),
					        		result.get(HabitQsState.GETUP).getString("text"),
					        		result.get(HabitQsState.IS_EXERCISE).getString("text"),
					        		result.get(HabitQsState.IS_DRINKING).getString("text"),
					        		result.get(HabitQsState.EAT_BREAKFAST).getString("text"),
					        		result.get(HabitQsState.EAT_SNACK).getString("text"),
					        		result.get(HabitQsState.SNACK_NAME).getString("text"));
					        String res = MyHttpCon.postHabit(nickName, lifeHabit);
						 	JSONObject data = new JSONObject(res);
						    boolean	success = data.getBoolean("success");
						 	if(success) {
						 		MyLog.info(TAG, "success");
						 	}
						 // </結果を送信>
						 	// モード更新
							Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTEN_CONDITION);
				        }
						// </生活習慣を聞き出す>
					}else if(mode == SotaState.Mode.LISTEN_CONDITION) {
						// 体調を聞き出す
						sotawish.Say("今日の体調はどんな感じ?");
						Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.FIN);
					}else if(mode == SotaState.Mode.FIN) {
						// 聞き取り終了
						sotawish.Say("今日の質問はこれで終わり。");
						String nameList = results.get(0).getString("furigana");
						// </sotaが認識した名前を繋げる>
						sotawish.Say(nameList + "さん,さようなら", MotionAsSotaWish.MOTION_TYPE_BYE);
						// 名前削除
						// リストは消すと減っていくから、先頭を常に消す
						Store.dispatch(Store.FIND_NAME_STATE, FindNameState.Action.REMOVE_NAME, 0);
						MyLog.info(TAG, "名前の数"+ findNameState.getResults());
						// 初めの状態に戻る
						Store.dispatch(Store.SOTA_STATE, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
					}
				}
				// 箱に直しやすいポーズにする
				MotionSample.stragePose(pose, mem, motion);
				// LED発光
		}
		}catch(Exception e) {
			CRobotUtil.Log(TAG, e.toString());
		}finally {
			 //サーボモータのトルクオフ
			  motion.ServoOff();
		}
	}
}
