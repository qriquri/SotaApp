package jp.hayamiti.httpCon;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JACKSONObject;
import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.ApiCom.BasicRes;
import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.httpCon.ApiCom.NameRecRes;
import jp.hayamiti.utils.MyLog;

public class ApiResponseTest {
	static final String TAG = "ApiResponse";
	static final String TEST_REC_PATH = "./test_rec.wav";
	static final String FIND_NAME_REC_PATH = "./find_name.wav";
	public static void main(String[] args) {
		try {
			JSONMapper.mapper.writeValueAsString(new BasicRes());
		} catch (JsonProcessingException e) {
			// TODO 自動生成された catch ブロック
			MyLog.error(TAG, e.toString());
		}
		try {
			MyLog.info(TAG, "speechRec");
			JACKSONObject res = MyHttpCon.speechRec(TEST_REC_PATH);;
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			MyLog.error(TAG, e.toString());
		}
		try {
			MyLog.info(TAG, "NameRec");
			NameRecRes res = MyHttpCon.nameRec(FIND_NAME_REC_PATH);
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			MyLog.error(TAG, e.toString());
		}

//		try {
//			MyLog.info(TAG, "yesOrNo");
//			String result = MyHttpCon.yesOrNo(TEST_REC_PATH);
//			YesOrNoRes res = JSONMapper.mapper.readValue(result, YesOrNoRes.class);
//			String json = JSONMapper.mapper.writeValueAsString(res);
//			MyLog.info(TAG, json);
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			MyLog.error(TAG, e.toString());
//		}

		try {
			MyLog.info(TAG, "habitQs");
			HabitQsRes res = MyHttpCon.habitQs(TEST_REC_PATH, "exercise");
			String json = JSONMapper.mapper.writeValueAsString(res);
			MyLog.info(TAG, json);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			MyLog.error(TAG, e.toString());
		}
	}
}
