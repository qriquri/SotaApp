package jp.hayamiti;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.hayamiti.JSON.JSONMapper;
import jp.hayamiti.httpCon.MyHttpCon;
import jp.hayamiti.httpCon.ApiCom.BasicRes;
import jp.hayamiti.httpCon.ApiCom.GenerateSentenceRes;
import jp.hayamiti.state.GenerateSentenceState;
import jp.hayamiti.state.SotaState;
import jp.hayamiti.state.SpRecState;
import jp.hayamiti.state.State;
import jp.hayamiti.state.Store;
import jp.hayamiti.state.TextToSpeechState;
import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CPlayWave;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;

public class GenerateSentence {
    static final String TAG = "GenerateSentence";
    static final String START_SOUND = "sound/mao-damasi-system04.wav";

    public static boolean generateSentence(String startSentence) {
        try {
        GenerateSentenceRes res = MyHttpCon.generateSentence(startSentence);
        Store.dispatch(GenerateSentenceState.class, GenerateSentenceState.Action.UPDATE_RESULT, res);
        return true;
        }catch(Exception e){
           CRobotUtil.Log(TAG, e.toString());
           return false;
        }
    }

    public static void main(String[] args) {
        // <JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
        try {
            JSONMapper.mapper.writeValueAsString(new BasicRes());
        } catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            MyLog.error(TAG, e.toString());
        }
        // </JSONMapperクラスのmapperはインスタンス生成に時間がかかるので、初めに生成しておく>
        CRobotPose pose = null;
        //VSMDと通信ソケット・メモリアクセス用クラス
        CRobotMem mem = new CRobotMem();
        //Sota用モーション制御クラス
        CSotaMotion motion = new CSotaMotion(mem);
        //sotawish初期化
        MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
        //マイク
        CRecordMic mic = new CRecordMic();
        try {
            //Store 初期化 stateを束ねる
            ArrayList<State> stateList = new ArrayList<State>() {
                {
                    add(new SotaState());
                    add(new SpRecState());
                    add(new TextToSpeechState());
                    add(new GenerateSentenceState());
                }
            };
            Store.bind(stateList);
            // 音声合成手法を設定
            Store.dispatch(TextToSpeechState.class, TextToSpeechState.Action.SET_METHOD,
                    TextToSpeechState.Method.SOTA_CLOUD);
            if (mem.Connect()) {
                //Sota仕様にVSMDを初期化
                motion.InitRobot_Sota();
                motion.ServoOn();
                GamingLED.off(pose, mem, motion);
                // 気を付けのポーズに戻す
                MotionSample.defaultPose(pose, mem, motion);
                //音声ファイル再生
                //raw　Waveファイルのみ対応
                CPlayWave.PlayWave(START_SOUND, false);
                GamingLED.on(pose, mem, motion);
                // sotaのモードをlisteningに変化
                Store.dispatch(SotaState.class, SotaState.Action.UPDATE_MODE, SotaState.Mode.LISTENING);
                CRobotUtil.wait(1000);
                // sotaに待機モーションをさせる
                sotawish.StartIdling();
                if(generateSentence("運動のメリットは")) {
                    final GenerateSentenceRes res = ((GenerateSentenceState)Store.getState(GenerateSentenceState.class)).getResult();
                    String result = res.getResult().get((int)(res.getResult().size() * Math.random()));
                    TextToSpeech.speech(result, sotawish, result);

                }
                // 箱に直しやすいポーズにする
                MotionSample.stragePose(pose, mem, motion);
            }
        } catch (Exception e) {
            CRobotUtil.Log(TAG, e.toString());
        } finally {
            //音声ファイル再生
            //raw　Waveファイルのみ対応
            CPlayWave.PlayWave(START_SOUND, false);
            GamingLED.off(pose, mem, motion);
            //サーボモータのトルクオフ
            motion.ServoOff();
        }
        return;
    }
}
