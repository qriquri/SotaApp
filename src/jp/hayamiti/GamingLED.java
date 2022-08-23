package jp.hayamiti;

import java.awt.Color;

import jp.vstone.RobotLib.CRobotMem;
import jp.vstone.RobotLib.CRobotPose;
import jp.vstone.RobotLib.CRobotUtil;
import jp.vstone.RobotLib.CSotaMotion;


public class GamingLED {

	static final String TAG = "GamingLED";
	static private int[] ledColor = {200, 200, 200};
	static private float colorAngle = 0;
	public static void on(CRobotPose pose, CRobotMem mem, CSotaMotion motion) {
	    pose = new CRobotPose();
//	    motion.ServoOn();
		ledColor[0] = (int)Math.abs((200 * Math.cos(colorAngle * Math.PI))) + 50;
        ledColor[1] = (int)Math.abs((200 * Math.sin(colorAngle * Math.PI))) + 50;
        ledColor[2] = (int)Math.abs((200 * Math.cos(colorAngle * 2 * Math.PI))) + 50;
        colorAngle += 0.1;
        pose.setLED_Sota(new Color(ledColor[0], ledColor[1], ledColor[2]), new Color(ledColor[0], ledColor[1], ledColor[2]), 255, new Color(ledColor[0], ledColor[1], ledColor[2]));
        motion.play(pose, 250);
        motion.waitEndinterpAll();
//	    motion.ServoOff();

	}

	public static void off(CRobotPose pose, CRobotMem mem, CSotaMotion motion) {
		CRobotUtil.Log(TAG, "off");
//		motion.ServoOn();
		pose = new CRobotPose();
		ledColor[0] = 0;
	    ledColor[1] = 0;
	    ledColor[2] = 0;
	    pose.setLED_Sota(new Color(ledColor[0], ledColor[1], ledColor[2]), new Color(ledColor[0], ledColor[1], ledColor[2]), 0, new Color(0, 255, 0));
	    motion.play(pose, 250);
	    motion.waitEndinterpAll();
//	    motion.ServoOff();
	}

}
