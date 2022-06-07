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
import java.util.UUID;

import org.json.JSONObject;

import jp.hayamiti.utils.MyLog;

public class MyHttpCon {
//	private static final String EOL = System.getProperty("line.separator");
	private static final String EOL = "\r\n"; // <= サーバーのosの改行コードに合わせる
    private static final String LOG_TAG = "MyHttpCon";
    public static final String API_HOME = "http://192.168.1.44:80"; // これ変わるから注意
    public static final String DB_HOME = "http://192.168.1.44:8000";// これ変わるから注意


    /**
     * ファイル転送
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static String uploadFile(String filename, String url) throws IOException {
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
    public static String speechRec(String filename) throws IOException {
    	String response = "{\"success\": false}";
        String url = API_HOME + "/spRec?sendTime=" + System.currentTimeMillis();
        response = uploadFile(filename, url);
        return response;
    }

    /**
     * 名前認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static String nameRec(String filename) throws IOException {
    	String response = "{\"success\": false}";
        String url = API_HOME + "/nameRec?sendTime=" + System.currentTimeMillis();
        response = uploadFile(filename, url);
        return response;
    }

    /**
     * yesNo判定
     * @param filename
     * @return
     * @throws IOException
     */
    public static String yesOrNo(String filename) throws IOException {
    	String response = "{\"success\": false}";
        String url = API_HOME + "/yesOrNo?sendTime=" + System.currentTimeMillis();
        response = uploadFile(filename, url);
        return response;
    }

    public static String getUserNames(String nameKana) throws IOException {
    	String response = "{\"success\": false}";
        String encodeName = URLEncoder.encode(nameKana,"UTF-8");
        String url = DB_HOME + "/getUserNames?nameKana="+ encodeName;
        response = createGetReq(url);
        return response;
    }

    public static String getTodayHabit(String nickName, boolean isSota) throws IOException {
       String response = "{\"success\": false}";
       String encodeName = URLEncoder.encode(nickName,"UTF-8");
       String url = DB_HOME + "/getTodayHabit?nickName="+encodeName+"&isSota="+isSota;
       response = createGetReq(url);
        return response;
    }

    public static String postHabit(String nickName, LifeHabit lifeHabit) throws IOException{
    	String response = "{\"success\": false}";
    	String url = DB_HOME + "/postHabit";
    	String body = "{\"nickName\":\"" + nickName + "\""
    					+",\"sleep\":" + lifeHabit.getSleep()
		    			+ ",\"getUp\":" + lifeHabit.getGetUp()
		    			+ ",\"exercise\":" + lifeHabit.getExercise()
		    			+ ",\"drinking\":" + lifeHabit.getDrinking()
		    			+ ",\"eatBreakfast\":" + lifeHabit.getEatBreakfast()
		    			+ ",\"eatSnack\":" + lifeHabit.getEatSnack()
		    			+ ",\"snackName\":\"" + lifeHabit.getSnackName()  + "\""
    					+ ",\"sleepT\":\"" + lifeHabit.getTextList()[0] + "\""
		    			+ ",\"getUpT\":\"" + lifeHabit.getTextList()[1] + "\""
		    			+ ",\"exerciseT\":\"" + lifeHabit.getTextList()[2] + "\""
		    			+ ",\"drinkingT\":\"" + lifeHabit.getTextList()[3]+ "\""
		    			+ ",\"eatBreakfastT\":\"" + lifeHabit.getTextList()[4] + "\""
		    			+ ",\"eatSnackT\":\"" + lifeHabit.getTextList()[5] + "\""
		    			+ ",\"snackNameT\":\"" + lifeHabit.getTextList()[6] + "\""
		    			+ "}";
    	try {
    		new JSONObject(body); // 一応ここでjsonエラーが出ないか確認する
    		response = sendJSON(body, url);
    	}catch(Exception e) {
    		MyLog.error(LOG_TAG, "postHabit err " + e.toString());
    		MyLog.error(LOG_TAG, "postHabit err " + body);
    	}finally {

    	}
    	return response;
    }

    /**
     * JSON形式で送信したいときに使う
     * @param body
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendJSON(String body, String url) throws IOException {
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
    private static String createGetReq(String url) throws IOException{
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
     * httpリクエストの返信を読み取る
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * httpリクエストでファイルを送り付ける
     * @param con
     * @param filename
     * @param file
     * @throws IOException
     */
    private static void sendFileHttp(HttpURLConnection con, String filename, FileInputStream file) throws IOException{
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
    public static String getMsg(String url) throws IOException {
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
    public static String SendMsg(String msg, String url) throws IOException {
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
