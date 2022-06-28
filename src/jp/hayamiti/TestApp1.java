package jp.hayamiti;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.hayamiti.httpCon.TestJSON;
import jp.vstone.RobotLib.CPlayWave;

public class TestApp1 {
	static final String TAG = "TestApp1";
	public static void main(String[] args) throws JsonProcessingException {
//		System.out.println(System.currentTimeMillis());
//		System.out.println(CRobotUtil.getTimeString());
//		System.out.println(CRobotUtil.getLocale());
//		System.out.println(CRobotUtil.getDateString());

		TestJSON test = new TestJSON();
		test.id = 10;
		test.name = "hoge";

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(test);

		System.out.println(json);

		json = "{\"id\":20, \"name\":\"HOGE\"}";

		TestJSON test2 = mapper.readValue(json, TestJSON.class);

		System.out.println(test2);
		try {
		json = "{\"id\":20, \"name\":\"HOGE\", \"year\": 100}";

		TestJSON test3 = mapper.readValue(json, TestJSON.class);

		System.out.println(test3);
		}catch(Exception e) {
			e.printStackTrace();
		}
		//音声ファイル再生
		//raw　Waveファイルのみ対応
		CPlayWave.PlayWave("sound/cursor10.wav", true);

		//音声ファイル再生
		//raw　Waveファイルのみ対応
		CPlayWave.PlayWave("sound/mao-damasi-onepoint23.wav", true);
	}
}
