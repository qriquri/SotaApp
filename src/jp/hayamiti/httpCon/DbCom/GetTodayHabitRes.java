package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class GetTodayHabitRes extends JACKSONObject{
	private GetTodayHabitResult result;
	private boolean success;
	public GetTodayHabitResult getResult() {
		return result;
	}
	public void setResult(GetTodayHabitResult result) {
		this.result = result;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
