package jp.hayamiti;

import jp.vstone.RobotLib.CRobotUtil;

public class TestApp1 {
	static final String TAG = "TestApp1";
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(CRobotUtil.getTimeString());
		System.out.println(CRobotUtil.getLocale());
		System.out.println(CRobotUtil.getDateString());
	}
}
