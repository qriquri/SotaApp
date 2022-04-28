package jp.hayamiti;

import java.awt.Color;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;


public class GamingLED {

	static final String TAG = "GamingLED";
	static private boolean isRunning = true;
	static private int[] ledColor = {200, 200, 200};
	static private float colorAngle = 0;
	public static void on(CRobotPose pose, CRobotMem mem, CSotaMotion motion) {
	    pose = new CRobotPose();
//		    motion.ServoOn();
		ledColor[0] = (int)Math.abs((200 * Math.cos(colorAngle * Math.PI))) + 50;
        ledColor[1] = (int)Math.abs((200 * Math.sin(colorAngle * Math.PI))) + 50;
        ledColor[2] = (int)Math.abs((200 * Math.cos(colorAngle * 2 * Math.PI))) + 50;
        colorAngle += 0.1;
//	        pose.SetPose(new Byte[]{1 ,2 ,3 ,4 ,5 ,6 ,7 ,8} //id
//			  , new Short[]{0 ,0,-900,0 , 900, 0, 0, 0} //target pos
//			  );
        pose.setLED_Sota(new Color(ledColor[0], ledColor[1], ledColor[2]), new Color(ledColor[0], ledColor[1], ledColor[2]), 255, new Color(ledColor[0], ledColor[1], ledColor[2]));
        motion.play(pose, 500);
        motion.waitEndinterpAll();
//	        motion.ServoOff();

	}
	public static void onAsSync(final CRobotMem mem, final CSotaMotion motion) {
		isRunning = true;
		new Thread(new Runnable() {
			public void run() {
				CRobotUtil.Log(TAG, "on");
				float angle = 0;
				while(mem.isConected() && isRunning) {
					CRobotPose pose = new CRobotPose();
					int r = (int)Math.abs((200 * Math.cos(angle * Math.PI))) + 50;
			        int g = (int)Math.abs((200 * Math.sin(angle * Math.PI))) + 50;
			        int b = (int)Math.abs((200 * Math.cos(angle * 2 * Math.PI))) + 50;angle += 0.03;
					angle += 0.03;
					//LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
					pose.setLED_Sota(new Color(r, g, b), new Color(r, g, b), r, new Color(r, g, b));
//					pose.setLED_Sota(new Color(255, 0, 0), new Color(r, g, b), r, new Color(0, 255, 0));

					CRobotUtil.Log(TAG, "color");
					System.out.print(r);System.out.print(" ");
					System.out.print(g);System.out.print(" ");
					System.out.println(b);
					motion.play(pose,1);

					motion.waitEndinterpAll();
					CRobotUtil.wait(1000);
				}
			}
		}).start();
	}

	public static void onSync(final CRobotMem mem, final CSotaMotion motion) {
		isRunning = true;
		CRobotUtil.Log(TAG, "on");
		float angle = 0;
		while(mem.isConected() && isRunning) {
			CRobotPose pose = new CRobotPose();
			int r = (int)Math.abs((200 * Math.cos(angle * Math.PI))) + 50;
	        int g = (int)Math.abs((200 * Math.sin(angle * Math.PI))) + 50;
	        int b = (int)Math.abs((200 * Math.cos(angle * 2 * Math.PI))) + 50;angle += 0.03;
			//LEDを点灯（左目：赤、右目：赤、口：Max、電源ボタン：赤）
			pose.setLED_Sota(new Color(r, g, b), new Color(r, g, b), 255, new Color(0, 255, 0));
//			pose.setLED_Sota(new Color(255, 0, 0), new Color(r, g, b), r, new Color(r, g, b));

			CRobotUtil.Log(TAG, "color");
			System.out.print(r);System.out.print(" ");
			System.out.print(g);System.out.print(" ");
			System.out.println(b);


			motion.play(pose,1);
			motion.waitEndinterpAll();
			CRobotUtil.wait(1000);

		}

	}

	public static void off() {
		CRobotUtil.Log(TAG, "off");
		isRunning = false;
	}

}
