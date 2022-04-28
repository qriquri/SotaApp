package jp.hayamiti.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.sotatalk.TextToSpeechSota;

public class MySocket {
    private static final String LOG_TAG = "MySocket";
    private static Socket sock;
    private static PrintWriter writer;
    private static BufferedReader reader;
    private static boolean isConnected = false;
    private static ArrayList<Listener> listeners = new ArrayList<Listener>();

    public static void connect(final String ip, final int port, final int timeOut){
                try {
                    // <ソケットや送信,受信に必要なストリームの設定>
                    InetSocketAddress address = new InetSocketAddress(ip, port);
                    sock = new Socket();
                    sock.connect(address, timeOut);
                    writer = new PrintWriter(sock.getOutputStream(), true);
                    reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
                    // </ソケットや送信,受信に必要なストリームの設定>
                    // <メンバ変数更新>
                    isConnected = true;
                    // </メンバ変数更新>

                } catch (Exception e) {
                    e.printStackTrace();

                    isConnected = false;
                }
                if(isConnected){
                    emit(MessageListener.CHANNEL, "HeiHei");
                    System.out.print(LOG_TAG);
                    System.out.println("サーバーと接続できました");
                    CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("サーバーと接続出来たよ"),true);
                    new Thread (new Runnable() {
                        public void run() {
                        	listen();
                        }
                    }).start();
                }else {
                	CPlayWave.PlayWave(TextToSpeechSota.getTTSFile("サーバーと接続出来なかったよ"),true);
                }
            
    }

    public static void emit(final String header, final String payload){
        if(!isConnected){
            System.out.print(LOG_TAG);
            System.out.println( "socket is not connected");
            return;
        }
        new Thread (new Runnable() {
            public void run() {
                try {
                    // <Json変換>
                    String p = payload.replace(" ", "<SPACE>").replace("/", "<SLASH>").replace("+", "<PLUS>").replace("=", "<EQUAL>");
                    // スペースを<SPACE>に変換する.そのままだと例外が発生する.
                    String str = "{\"payload\":" + p + ", \"channel\":" + header + "}";
//                    JSONObject jsonObject = new JSONObject(str);
                    System.out.print(LOG_TAG);
                    System.out.println("send:" + header);
                    // </Json変換>
                    // 送信
                    writer.println(str);
                } catch (
                        Exception e) {
                    System.out.print(LOG_TAG);
                    System.out.println( "emit error:" + e);

                }
            }
        }).start();
    }

    public static void disconnect(){
        emit(MessageListener.CHANNEL, "end");
        close();
    }

    private static void close(){
        isConnected = false;
        try {
            reader.close();
            writer.close();
            sock.close();
            listeners.clear();
            System.out.print(LOG_TAG);
            System.out.println("切断しました");
        }catch (IOException e){

            System.out.print(LOG_TAG);
            System.out.println("disconnect error:" + e);
        }
    }

    /**
     * リスナー追加
     * @param listener リスナー
     */
    public static void on(Listener listener){
        listeners.add(listener);
    }

    /**
     * リスナー削除
     * @param listener リスナー
     */
    public static void off(Listener listener){
        listeners.remove(listener);
    }

    private static void listen(){
        while(isConnected){
            try {
                // <bufferReader から読み取る>
                String data = reader.readLine();
                // </bufferReader から読み取る>
                // <読み取ったデータをjsonに直して、リスナーの処理を行う>
                try {
                    JSONObject jsonData = new JSONObject(data);
                    for(int i = 0; i < listeners.size(); i++){
                        // jsonデータとリスナーのchannelが同じとき
                        if(jsonData.getString("channel").equals(listeners.get(i).getChannel())){
                            listeners.get(i).callback(jsonData);
                            break;
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                // </読み取ったデータをjsonに直して、リスナーの処理を行う>
            } catch (IOException e) {

                System.out.print(LOG_TAG);
                System.out.println("listen error :" + e);

                // <socket close>
                close();
                // </socket close>
//                connect(activity, ip, port, 3000);
            } catch (NullPointerException e){

                System.out.print(LOG_TAG);
                System.out.println("listen error :" + e);
                // <socket close>
                close();
                // </socket close>
            }
        }
    }

}
