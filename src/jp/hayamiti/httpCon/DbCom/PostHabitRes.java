package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

final public class PostHabitRes extends JACKSONObject{
	 boolean success;

	final public boolean isSuccess() {
		return success;
	}

	final public void setSuccess(boolean success) {
		this.success = success;
	}
}