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

import jp.hayamiti.utils.MyLog;

public class MyHttpCon {
//	private static final String EOL = System.getProperty("line.separator");
	private static final String EOL = "\r\n";
    private static final String LOG_TAG = "MyHttpCon";
    public static final String API_HOME = "http://192.168.1.40:80"; // これ変わるから注意
    public static final String DB_HOME = "http://192.168.1.40:8000";

    public static String getMsg(String url) throws IOException {
        HttpURLConnection con = null;
        InputStream inputStream = null;
        String response = "";
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

    public static String SendMsg(String msg, String url) throws IOException {
        String response = "";
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

    /**
     * ファイル転送
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(String filename, String url) throws IOException {
        boolean isSuccess = false;
        HttpURLConnection con = null;
        FileInputStream file = null;
        try {
            // <httpリクエスト設定>
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            sendFileHttp(con, filename, file);
            // </httpリクエスト設定>
            if (con.getResponseCode() == 200) {
                isSuccess = true;
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
        }
        return isSuccess;
    }

    /**
     * 音声認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static String speechRec(String filename, String url) throws IOException {
        HttpURLConnection con = null;
        FileInputStream file = null;
        InputStream inputStream = null;
        String response = "";
        try {
            // <httpリクエスト設定>
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            sendFileHttp(con, filename, file);
            // </httpリクエスト設定>
            // <結果の読み取り>
            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
            // </結果の読み取り>

        } catch (Exception e) {
            MyLog.error(LOG_TAG, "uploadFile" + e.toString());
        } finally {
            if (con != null) {
            con.disconnect();
            }
            if(file != null){
                file.close();
            }
        }
        return response;
    }

    /**
     * 名前認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static String nameRec(String filename, String url) throws IOException {
        HttpURLConnection con = null;
        FileInputStream file = null;
        InputStream inputStream = null;
        String response = "";
        try {
            // <httpリクエスト設定>
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            sendFileHttp(con, filename, file);
            // </httpリクエスト設定>
            // <結果の読み取り>
            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
            // </結果の読み取り>

        } catch (Exception e) {
            MyLog.error(LOG_TAG, "uploadFile" + e.toString());
        } finally {
            if (con != null) {
            con.disconnect();
            }
            if(file != null){
                file.close();
            }
        }
        return response;
    }

    public static String getUserNames(String url, String nameKana) throws IOException {
        HttpURLConnection con = null;
        InputStream inputStream = null;
        String response = "";
        try {
        	String encodedName = URLEncoder.encode(nameKana, "UTF-8"); // 文字化け対策
            con = (HttpURLConnection) new URL(url + "?nameKana="+encodedName).openConnection();
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
     * 名前認識
     * @param filename
     * @param url
     * @return
     * @throws IOException
     */
    public static String yesOrNo(String filename) throws IOException {
        HttpURLConnection con = null;
        FileInputStream file = null;
        InputStream inputStream = null;
        String response = "";
        try {
            // <httpリクエスト設定>
        	String url = API_HOME + "/yesOrNo" + "?sendTime="+System.currentTimeMillis();
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            sendFileHttp(con, filename, file);
            // </httpリクエスト設定>
            // <結果の読み取り>
            if (con.getResponseCode() == 200) {
                inputStream = con.getInputStream();
                response = readFromStream(inputStream);
            }
            // </結果の読み取り>

        } catch (Exception e) {
            MyLog.error(LOG_TAG, "uploadFile" + e.toString());
        } finally {
            if (con != null) {
            con.disconnect();
            }
            if(file != null){
                file.close();
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
}
