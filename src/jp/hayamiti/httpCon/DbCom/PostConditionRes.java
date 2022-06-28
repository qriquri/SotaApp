package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class PostConditionRes extends JACKSONObject{
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}