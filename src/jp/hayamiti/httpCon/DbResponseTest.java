package jp.hayamiti.httpCon;

import java.io.IOException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.DbCom.GetTodayHabitRes;
import jp.hayamiti.httpCon.DbCom.GetUserNamesRes;
import jp.hayamiti.httpCon.DbCom.PostConditionRes;
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
			String response = MyHttpCon.getUserNames("タケダ");
			GetUserNamesRes res = JSONMapper.mapper.readValue(response, GetUserNamesRes.class);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

		try {
			MyLog.info(TAG, "postHabit");
			String response = MyHttpCon.postHabit("", new LifeHabit());
			PostHabitRes res = JSONMapper.mapper.readValue(response, PostHabitRes.class);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

		try {
			MyLog.info(TAG, "postCondition");
			String response = MyHttpCon.postCondition("", "元気", "元気");
			PostConditionRes res = JSONMapper.mapper.readValue(response, PostConditionRes.class);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			MyLog.error(TAG, e.toString());
		}

	}
}
