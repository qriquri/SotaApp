package jp.hayamiti;


import java.io.File;
import java.net.URI;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import jp.hayamiti.utils.MyLog;
import jp.hayamiti.websocket.MessageListener;
import jp.hayamiti.websocket.MyWsClient;

public class Main {
    final static String LOG_TAG = "main";

    public static void main(String[] args) {
        try {
            MyWsClient.on(new MessageListener());
            MyWsClient client = new MyWsClient(new URI("ws://192.168.10.102:8000"));
            client.connect();
            // AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
            // "C:\\Users\\qripu\\Documents\\GitHub\\typescript_projects\\fileSaveSample\\src\\java\\sample.wav"));
            // byte[] wavData = new byte[stream.available()];
            // stream.read(wavData, 0, wavData.length);
            File audioFile = new File(
                    "C:\\Users\\qripu\\Documents\\GitHub\\typescript_projects\\fileSaveSample\\src\\java\\sample3.wav");
            byte[] bytes = FileUtils.readFileToByteArray(audioFile);
            String encoded = Base64.getEncoder().encodeToString(bytes);
            MyWsClient.emit("AUDIO", encoded);
        } catch (Exception e) {
            MyLog.error(LOG_TAG, e.toString());
        }
    }
}
