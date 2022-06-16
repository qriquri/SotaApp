package jp.hayamiti;

import jp.hayamiti.utils.MyLog;
import jp.vstone.RobotLib.CRecordMic;
import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;
import jp.vstone.sotatalk.MotionAsSotaWish;
import lib.usb.Hiddata;

public class JinkanTest {
	public static final String TAG = "JinkanTest";
	private static int nMode = 0;
	private static int pMode = 0;
	public static void main(String[] args) {
		System.loadLibrary("Hiddata");
		CRobotPose pose = null;
		//VSMDと通信ソケット・メモリアクセス用クラス
		CRobotMem mem = new CRobotMem();
		//Sota用モーション制御クラス
		CSotaMotion motion = new CSotaMotion(mem);
		//sotawish初期化
		MotionAsSotaWish sotawish = new MotionAsSotaWish(motion);
		//マイク
		CRecordMic mic = new CRecordMic();
		if(mem.Connect()){
	        motion.InitRobot_Sota();
	        motion.ServoOn();
	        MotionSample.defaultPose(pose, mem, motion);
	        CRobotUtil.Log(TAG, "isIdling:" + sotawish.isPlayIdling());
			try{
				Hiddata hiddata = new Hiddata();
	            int dev = hiddata.usbhidOpenDevice(0x16c0, 0x05df);
	            if(dev == 0){
	                MyLog.info(TAG,"Sensor not found.");
	            }else{
	            	 new Thread(new Runnable() {
	                     public void run() {
	                    	 while(true) {
	                    		 if(nMode == 2) {
			                    	GamingLED.on(pose, mem, motion);

			                    }else if(nMode == 0) {
			                    	GamingLED.off(pose, mem, motion);

			                    }
	                    	 }
	                     }
	                 }).start();
	            	while(true) {
		                System.out.println("Sensor found");
		                    byte[] buffer = new byte[17];
		                    if(hiddata.usbhidGetReport(dev, 0, buffer, buffer.length) == 0){
		                        MyLog.info(TAG,"value:" + buffer[1]);
		                        pMode = nMode;
		                        nMode = buffer[1];
		                    }else{
		                        MyLog.info(TAG,"Sensor read error");
		                        break;
		                    }
		                    if(pMode == 0 && nMode == 2) {
		                    	CRobotUtil.Log(TAG, "Start Idling");
//		                    	motion.ServoOn();
		                    	sotawish.StartIdling();
//		                    	CRobotUtil.Log(TAG, "isIdling:" + sotawish.isPlayIdling());
		                    }else if (pMode == 2 && nMode == 0) {
		                    	CRobotUtil.Log(TAG, "Stop Idling");
		                    	sotawish.StopIdling();
		                    	MotionSample.defaultPose(pose, mem, motion);
//		                    	CRobotUtil.Log(TAG, "isIdling:" + sotawish.isPlayIdling());

		                    }
		                    if(nMode == 2) {
		                    	Thread.sleep(5000);
		                    }else {
		                    	Thread.sleep(500);
		                    }
		            }
	            	hiddata.usbhidCloseDevice(dev);
	            }
			}catch(InterruptedException e){
	            e.printStackTrace();
	        }
			// 箱に直しやすいポーズにする
			MotionSample.stragePose(pose, mem, motion);
			motion.ServoOff();
		}
	}
}
