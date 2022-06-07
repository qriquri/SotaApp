package jp.hayamiti;
//
//import java.io.IOException;
//
//import org.json.JSONObject;
//
//import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.utils.MyLog;
//
public class SendHabit {
	public static final String LOG_TAG = "sendHabit";
	public static void main(String args[]) {
		MyLog.info(LOG_TAG, "hello world");
	}}
////		MyHttpCon.getTodayHabit("シンゲン", false);
//		MyHttpCon.postHabit("タロウ", 20, 10, false, false, false, true, "ポテトチップス");
//		try {
//		String result = MyHttpCon.getTodayHabit("シンゲン", true);
//		JSONObject data = new JSONObject(result);
//		Boolean success = data.getBoolean("success");
//		if(success) {
//			MyLog.info(LOG_TAG,  "show Result " + data.getJSONObject("result").toString());
//			result = MyHttpCon.postHabit("シンゲン", 20, 10, false, false, false, true, "ポテトチップス チョコレート");
//			data = new JSONObject(result);
//		 	success = data.getBoolean("success");
//		 	if(!success) {
//		 		MyLog.info(LOG_TAG, "登録済み");
//		 	}
//		}else {
//		 	result = MyHttpCon.postHabit("シンゲン", 20, 10, false, false, false, true, "ポテトチップス チョコレート");
//		 	data = new JSONObject(result);
//		 	success = data.getBoolean("success");
//		 	if(success) {
//		 		MyLog.info(LOG_TAG, "success");
//		 	}
//		}
//		}catch (Exception e) {
//
//		}
//
//		try {
//			String result = MyHttpCon.getTodayHabit("たけよし", true);
//			JSONObject data = new JSONObject(result);
//			Boolean success = data.getBoolean("success");
//			if(success) {
//				MyLog.info(LOG_TAG,  "show Result " + data.getJSONObject("result").toString());
//				result = MyHttpCon.postHabit("たけよし", 40, -10, false, false, false, true, "ポテトチップス チョコレート");
//				data = new JSONObject(result);
//			 	success = data.getBoolean("success");
//			 	if(!success) {
//			 		MyLog.info(LOG_TAG, "登録済み");
//			 	}
//			}else {
//			 	result = MyHttpCon.postHabit("たけよし", 40, -10, false, false, false, true, "ポテトチップス チョコレート");
//			 	data = new JSONObject(result);
//			 	success = data.getBoolean("success");
//			 	if(success) {
//			 		MyLog.info(LOG_TAG, "success");
//			 	}
//			}
//		}catch (Exception e) {
//
//		}
//
//		try {
//			String result = MyHttpCon.getTodayHabit("たけよし", true);
//			JSONObject data = new JSONObject(result);
//			Boolean success = data.getBoolean("success");
//			if(success) {
//				MyLog.info(LOG_TAG,  "show Result " + data.getJSONObject("result").toString());
//				result = MyHttpCon.postHabit("たけよし", 4, 10, false, false, false, true, "うまい棒");
//				data = new JSONObject(result);
//			 	success = data.getBoolean("success");
//			 	if(!success) {
//			 		MyLog.info(LOG_TAG, "登録済み");
//			 	}
//			}else {
//			 	result = MyHttpCon.postHabit("たけよし", 4, 10, false, false, false, true, "うまい棒");
//			 	data = new JSONObject(result);
//			 	success = data.getBoolean("success");
//			 	if(success) {
//			 		MyLog.info(LOG_TAG, "success");
//			 	}
//			}
//		}catch (Exception e) {
//
//		}
//
//	}
//}
