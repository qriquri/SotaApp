package jp.hayamiti;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.TestJSON;
import jp.hayamiti.httpCon.DbCom.GetHabitsRes;
import jp.hayamiti.utils.MyLog;

public class TestApp1 {
	static final String TAG = "TestApp1";
	public static void main(String[] args) throws JsonProcessingException {

		TestJSON test = new TestJSON();
		test.id = 10;
		test.name = "hoge";

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(test);

		MyLog.info(TAG,json);

		json = "{\"id\":20, \"name\":\"HOGE\"}";

		TestJSON test2 = mapper.readValue(json, TestJSON.class);

		MyLog.info(TAG, test2.toString());
//		try {
//		json = "{\"id\":20, \"name\":\"HOGE\", \"year\": 100}";
//
//		TestJSON test3 = mapper.readValue(json, TestJSON.class);
//
//		System.out.println(test3);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}

		try {
			String result = MyHttpCon.getHabits("たけよし", true, 1,1);
			GetHabitsRes res = JSONMapper.mapper.readValue(result, GetHabitsRes.class);
			MyLog.info(TAG, JSONMapper.mapper.writeValueAsString(res));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
//		//音声ファイル再生
//		//raw　Waveファイルのみ対応
//		CPlayWave.PlayWave("sound/cursor10.wav", true);

//		//音声ファイル再生
//		//raw　Waveファイルのみ対応
//		CPlayWave.PlayWave("sound/mao-damasi-onepoint23.wav", true);
	}
}
