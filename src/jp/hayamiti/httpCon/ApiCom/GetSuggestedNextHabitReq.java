package jp.hayamiti.httpCon.ApiCom;

import jp.hayamiti.JSON.JACKSONObject;

public class GetSuggestedNextHabitReq extends JACKSONObject {
	private int[] habit;

	public int[] getHabit() {
		return habit;
	}

	public void setHabit(int[] habit) {
		this.habit = habit;
	}
}
