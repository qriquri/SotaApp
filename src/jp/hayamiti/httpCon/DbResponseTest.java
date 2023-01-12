package jp.hayamiti.httpCon;

import java.io.IOException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.DbCom.GetTodayHabitRes;
import jp.hayamiti.httpCon.DbCom.GetUserNamesRes;
import jp.hayamiti.httpCon.DbCom.PostConditionReq;
import jp.hayamiti.httpCon.DbCom.PostConditionRes;
import jp.hayamiti.httpCon.DbCom.PostHabitReq;
import jp.hayamiti.httpCon.DbCom.PostHabitRes;
import jp.hayamiti.utils.MyLog;

public class DbResponseTest  {
	private static final String TAG = "DbResponseTest";
	public static void main(String[] args) {
		try {
			MyLog.info(TAG, "getTodayHabit");
			String response = MyHttpCon.getTodayHabit("シンゲン", true);
			GetTodayHabitRes res = JSONMapper.mapper.readValue(response, GetTodayHabitRes.class);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
//			e.printStackTrace();
			MyLog.error(TAG, e.toString());
		}

		try {
			MyLog.info(TAG, "getUserNames");
			GetUserNamesRes res = MyHttpCon.getUserNames("タケダ");
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

		try {
			MyLog.info(TAG, "postHabit");
			PostHabitReq req = new PostHabitReq();
			MyLog.info(TAG, JSONMapper.mapper.writeValueAsString(req));
			PostHabitRes res = MyHttpCon.postHabit(req);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

		try {
			MyLog.info(TAG, "postCondition");
			PostConditionReq req = new PostConditionReq();
			req.setNickName("");
			req.setCondition("元気");
			req.setSentence("元気");
			MyLog.info(TAG, JSONMapper.mapper.writeValueAsString(req));
			PostConditionRes res = MyHttpCon.postCondition(req);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

	}
}
