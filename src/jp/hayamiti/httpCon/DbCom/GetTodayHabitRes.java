package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

final public class GetTodayHabitRes extends JACKSONObject{
	private GetTodayHabitResult result;
	private boolean success;
	final public GetTodayHabitResult getResult() {
		return result;
	}
	final public void setResult(GetTodayHabitResult result) {
		this.result = result;
	}
	final public boolean isSuccess() {
		return success;
	}
	final public void setSuccess(boolean success) {
		this.success = success;
	}
}
