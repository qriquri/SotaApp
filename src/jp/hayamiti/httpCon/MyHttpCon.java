package jp.hayamiti.httpCon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.ApiCom.ConditionQsRes;
import jp.hayamiti.httpCon.ApiCom.DayQsRes;
import jp.hayamiti.httpCon.ApiCom.GenerateSentenceRes;
import jp.hayamiti.httpCon.ApiCom.GetSuggestedNextHabitReq;
import jp.hayamiti.httpCon.ApiCom.GetSuggestedNextHabitRes;
import jp.hayamiti.httpCon.ApiCom.HabitQsRes;
import jp.hayamiti.httpCon.ApiCom.NameRecRes;
import jp.hayamiti.httpCon.ApiCom.SpRecRes;
import jp.hayamiti.httpCon.ApiCom.YesOrNoReq;
import jp.hayamiti.httpCon.ApiCom.YesOrNoRes;
import jp.hayamiti.httpCon.DbCom.GetHabitsRes;
import jp.hayamiti.httpCon.DbCom.GetSuggestedHabitRes;
import jp.hayamiti.httpCon.DbCom.GetUserNamesRes;
import jp.hayamiti.httpCon.DbCom.PostConditionReq;
import jp.hayamiti.httpCon.DbCom.PostConditionRes;
import jp.hayamiti.httpCon.DbCom.PostHabitReq;
import jp.hayamiti.httpCon.DbCom.PostHabitRes;
import jp.hayamiti.httpCon.DbCom.PostSuggestedHabitReq;
import jp.hayamiti.httpCon.DbCom.PostSuggestedHabitRes;
import jp.hayamiti.utils.MyLog;

final public class MyHttpCon {
//	private static final String EOL = System.getProperty("line.separator");
	private static final String EOL = "\r\n"; // <= サーバーのosの改行コードに合わせる
    private static final String LOG_TAG = "MyHttpCon";
    public static final String API_HOME = "http://192.168.1.93:80"; // これ変わるから注意
    public static final String GPT2_API_HOME = "http://192.168.1.93:70"; // これ変わるから注意
    public static final String DB_HOME = "http://192.168.1.93:8000";// これ変わるから注意


    /**
     * ファイル転送
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static String uploadFile(String filename, String url) throws IOException {
        HttpURLConnection con = null;
        FileInputStream file = null;
        InputStream inputStream = null;
        String response = "{\"success\": false}";
        try {
            // <httpリクエスト設定>
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            sendFileHttp(con, filename, file);
            // </httpリクエスト設定>
            if (con.getResponseCode() == 200) {
            	inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }

        } catch (Exception e) {
            MyLog.error(LOG_TAG, "uploadFile" + e.toString());
        } finally {
            if (con != null) {
            con.disconnect();
            }
            if(file != null){
                file.close();
            }
            if(inputStream != null) {
            	inputStream.close();
            }
        }
        return response;
    }

    /**
     * 音声認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static SpRecRes speechRec(String filename) throws IOException {
    	String url = API_HOME + "/spRec?sendTime=" + System.currentTimeMillis();
        SpRecRes response = JSONMapper.mapper.readValue(uploadFile(filename, url), SpRecRes.class);

        return response;
    }

    /**
     * 音声合成
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static byte[] openJTalkRec(String text) throws IOException {
    	byte[] response = null;
        String url = API_HOME + "/openJTalk?&text=" + URLEncoder.encode(text, "UTF-8");
        response = createGetReqByByte(url);
        return response;
    }
    /**
     * 名前認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static NameRecRes nameRec(String text) throws IOException {
    	String url = API_HOME + "/nameRec?sendTime=" + System.currentTimeMillis() + "&text=" + URLEncoder.encode(text, "UTF-8");
        NameRecRes response = JSONMapper.mapper.readValue(createGetReq(url), NameRecRes.class);

        return response;
    }

    /**
     * 日にち認識（n日のnを取得する）
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    final public static DayQsRes dayRec(String text) throws IOException {
    	String url = API_HOME + "/dayRec?sendTime=" + System.currentTimeMillis() + "&text=" +  URLEncoder.encode(text, "UTF-8");
        DayQsRes response = JSONMapper.mapper.readValue(createGetReq(url), DayQsRes.class);
        return response;
    }

    /**
     * yesNo判定
     * @param filename
     * @return
     * @throws IOException
     */
    final public static YesOrNoRes yesOrNo(List<String> alternative) throws IOException {
    	YesOrNoReq req = new YesOrNoReq();
    	req.setAlternative(alternative);
    	String body = JSONMapper.mapper.writeValueAsString(req);
        String url = API_HOME + "/yesOrNo?sendTime=" + System.currentTimeMillis();
        YesOrNoRes response = JSONMapper.mapper.readValue(sendJSON(body,url), YesOrNoRes.class);

        return response;
    }

    /**
     * 生活習慣に関する質問の回答を解析
     * @param filename
     * @param type
     * @return
     * @throws IOException
     */
    final public static HabitQsRes habitQs(String text, String type) throws IOException {
    	String url = API_HOME + "/habitQs?sendTime=" + System.currentTimeMillis() + "&type=" + type + "&text=" + URLEncoder.encode(text, "UTF-8");
        HabitQsRes response = JSONMapper.mapper.readValue(createGetReq(url), HabitQsRes.class);
        return response;
    }

    /**
     * 体調の回答を解析
     * @param filename
     * @return
     * @throws IOException
     */
    final public static ConditionQsRes conditionQs(String text) throws IOException {
    	String url = API_HOME + "/conditionQs?sendTime=" + System.currentTimeMillis() + "&text=" + URLEncoder.encode(text, "UTF-8");
        ConditionQsRes response = JSONMapper.mapper.readValue(createGetReq(url), ConditionQsRes.class);

        return response;
    }

    /**
     * 次の週の改善目標を教えてもらう
     * @param habit
     * @return
     * @throws IOException
     */
    final public static GetSuggestedNextHabitRes getSuggestedNextHabit(int[] habit) throws IOException{
    	String url = API_HOME + "/getSuggestedNextHabit";
    	GetSuggestedNextHabitReq req = new GetSuggestedNextHabitReq();
    	req.setHabit(habit);
    	String body = JSONMapper.mapper.writeValueAsString(req);
    	GetSuggestedNextHabitRes response = JSONMapper.mapper.readValue(sendJSON(body, url), GetSuggestedNextHabitRes.class);
    	return response;
    }

    final public static GetUserNamesRes getUserNames(String nameKana) throws IOException {
    	String encodeName = URLEncoder.encode(nameKana,"UTF-8");
        String url = DB_HOME + "/getUserNames?nameKana="+ encodeName;
        GetUserNamesRes response = JSONMapper.mapper.readValue(createGetReq(url), GetUserNamesRes.class);

        return response;
    }

    final public static String getTodayHabit(String nickName, boolean isSota) throws IOException {
       String response = "{\"success\": false}";
       String encodeName = URLEncoder.encode(nickName,"UTF-8");
       String url = DB_HOME + "/getTodayHabit?nickName="+encodeName+"&isSota="+isSota;
       response = createGetReq(url);
        return response;
    }

    /**
     * 例えば3日前から7日前までなら、start=3, end = 7にする
     * @param nickName
     * @param isSota
     * @param start 何日前から
     * @param end   何日前まで
     * @return
     * @throws IOException
     */
    final public static String getHabits(String nickName, boolean isSota, int start, int end) throws IOException {
        String response = "{\"success\": false}";
        String encodeName = URLEncoder.encode(nickName,"UTF-8");
        String url = DB_HOME + "/getHabits?nickName="+encodeName+"&isSota="+isSota+"&start="+start+"&end="+end;
        response = createGetReq(url);
        return response;
     }

    /**
     * 例えば3日前から7日前までなら、start=3, end = 7にする
     * @param nickName
     * @param isSota
     * @param start 何日前から
     * @param end   何日前まで
     * @return
     * @throws IOException
     */
    final public static GetHabitsRes getOneWeekHabits(String nickName, boolean isSota, int backWeek) throws IOException {
        String encodeName = URLEncoder.encode(nickName,"UTF-8");
        String url = DB_HOME + "/getOneWeekHabits?nickName="+encodeName+"&isSota="+isSota+"&backWeek="+ backWeek;
        final GetHabitsRes response = JSONMapper.mapper.readValue(createGetReq(url),GetHabitsRes.class);
        return response;
     }

    final public static GetSuggestedHabitRes getSuggestedHabit(String nickName, int backWeek) throws IOException {
        String encodeName = URLEncoder.encode(nickName,"UTF-8");
        String url = DB_HOME + "/getSuggestedHabit?nickName="+encodeName+"&backWeek="+ backWeek;
        final GetSuggestedHabitRes response = JSONMapper.mapper.readValue(createGetReq(url),GetSuggestedHabitRes.class);
        return response;
     }

    final public static PostHabitRes postHabit(PostHabitReq req) throws IOException{
		String url = DB_HOME + "/postHabit";
    	String body = JSONMapper.mapper.writeValueAsString(req);
		PostHabitRes response = JSONMapper.mapper.readValue(sendJSON(body, url), PostHabitRes.class);
    	return response;
    }

    /**
     * 文章生成
     * @param startSentencel
     * @return
     * @throws IOException
     */
    final public static GenerateSentenceRes generateSentence(String startSentence) throws IOException {
        String url = GPT2_API_HOME + "/generateSentence?&text=" + URLEncoder.encode(startSentence, "UTF-8");
        GenerateSentenceRes jRes = JSONMapper.mapper.readValue(createGetReq(url), GenerateSentenceRes.class);
        return jRes;
    }

    final public static PostSuggestedHabitRes postSuggestedHaibt(PostSuggestedHabitReq req) throws IOException{
	    	String url = DB_HOME + "/postSuggestedHabit";
	    	String body = JSONMapper.mapper.writeValueAsString(req);

            PostSuggestedHabitRes response = JSONMapper.mapper.readValue(sendJSON(body, url), PostSuggestedHabitRes.class);

    	return response;
    }

    final public static PostConditionRes postCondition(PostConditionReq req) throws IOException{
		String url = DB_HOME + "/postCondition";
		String body = JSONMapper.mapper.writeValueAsString(req);
		PostConditionRes response = JSONMapper.mapper.readValue(sendJSON(body, url), PostConditionRes.class);
    	return response;
    }

    /**
     * JSON形式で送信したいときに使う
     * @param body
     * @param url
     * @return
     * @throws IOException
     */
    final public static String sendJSON(String body, String url) throws IOException {
        String response = "{\"success\": false}";
        HttpURLConnection con = null;
        InputStream inputStream = null;
        con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        try {
            OutputStream out = con.getOutputStream();
            out.write((body)
                    .getBytes(StandardCharsets.UTF_8));

            out.flush();

            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
        } catch (Exception e) {
            MyLog.error(LOG_TAG, "sendJson" + e.toString());
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    /**
     * GETリクエストを送り、レスポンスを得る
     * @param url
     * @return
     * @throws IOException
     */
    final private static String createGetReq(String url) throws IOException{
    	 HttpURLConnection con = null;
         InputStream inputStream = null;
         String response = "{\"success\": false}";
         try {
         	 con = (HttpURLConnection) new URL(url).openConnection();
             con.setRequestMethod("GET");
             con.setReadTimeout(10000);
             con.setConnectTimeout(15000);
             con.connect();
             if (con.getResponseCode() == 200) {
            	 // 成功したとき, レスポンスを読み取る
                 inputStream = con.getInputStream();
                 response = readFromStream(inputStream);
             }
         } catch (Exception e) {
             MyLog.error(LOG_TAG, "createGetReq" + e.toString());
         } finally {
             if (con != null) {
                 con.disconnect();
             }
             if (inputStream != null) {
                 inputStream.close();
             }
         }
         return response;
    }

    /**
     * GETリクエストを送り、レスポンスを得る
     * @param url
     * @return
     * @throws IOException
     */
    final private static byte[] createGetReqByByte(String url) throws IOException{
    	 HttpURLConnection con = null;
         InputStream inputStream = null;
         byte[] response = null;
         try {
         	 con = (HttpURLConnection) new URL(url).openConnection();
             con.setRequestMethod("GET");
             con.setReadTimeout(10000);
             con.setConnectTimeout(15000);
             con.connect();
             if (con.getResponseCode() == 200) {
            	 // 成功したとき, レスポンスを読み取る
                 inputStream = con.getInputStream();
                 response = readFromStreamByByte(inputStream);
             }
         } catch (Exception e) {
             MyLog.error(LOG_TAG, "createGetReq" + e.toString());
         } finally {
             if (con != null) {
                 con.disconnect();
             }
             if (inputStream != null) {
                 inputStream.close();
             }
         }
         return response;
    }

    /**
     * httpリクエストの返信を読み取る
     * @param inputStream
     * @return
     * @throws IOException
     */
    final private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
            	if(output.length() == 0) {
            		output.append(line);

            	}else {
            		output.append("\n"+line);

            	}
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * httpリクエストの返信を読み取る
     * @param inputStream
     * @return
     * @throws IOException
     */
    final private static byte[] readFromStreamByByte(InputStream inputStream) throws IOException {
    	byte[] output = null;
    	if (inputStream != null) {
    		// バイト配列に変換する
            output = IOUtils.toByteArray(inputStream);
        }
        return output;
    }


    /**
     * httpリクエストでファイルを送り付ける
     * @param con
     * @param filename
     * @param file
     * @throws IOException
     */
    final private static void sendFileHttp(HttpURLConnection con, String filename, FileInputStream file) throws IOException{
        // <httpリクエスト設定>
        final String boundary = UUID.randomUUID().toString();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        // </httpリクエスト設定>
        // <送信>
        OutputStream out = con.getOutputStream();
        out.write(("--" + boundary + EOL +
                "Content-Disposition: form-data; name=\"file\"; " +
                "filename=\"" + filename + "\"" + EOL + EOL)
                .getBytes(StandardCharsets.UTF_8));
        // <ファイルの中身を追加>
        file = new FileInputStream(filename);
        byte[] buffer = new byte[128];
        int size = -1;
        while (-1 != (size = file.read(buffer))) {
            out.write(buffer, 0, size);
        }
        // </ファイルの中身を追加>
        out.write((EOL + "--" + boundary + "--" + EOL).getBytes(StandardCharsets.UTF_8));
        out.flush();
        file.close();
        // </送信>
    }

    /**
     * テスト用
     * @param url
     * @return
     * @throws IOException
     */
    final public static String getMsg(String url) throws IOException {
        HttpURLConnection con = null;
        InputStream inputStream = null;
        String response = "{\"success\": false}";
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.connect();
            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
        } catch (Exception e) {
            MyLog.error(LOG_TAG, "getMsg" + e.toString());
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    /**
     * テスト用
     * @param msg
     * @param url
     * @return
     * @throws IOException
     */
    final public static String SendMsg(String msg, String url) throws IOException {
    	 String response = "{\"success\": false}";
        HttpURLConnection con = null;
        InputStream inputStream = null;
        con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        try {
            OutputStream out = con.getOutputStream();
            out.write(("{\"msg\": \"" + msg + "\"}")
                    .getBytes(StandardCharsets.UTF_8));

            out.flush();

            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
        } catch (Exception e) {
            MyLog.error(LOG_TAG, "sendMsg" + e.toString());
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return response;
    }
}
