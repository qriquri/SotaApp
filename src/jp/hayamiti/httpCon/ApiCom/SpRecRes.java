package jp.hayamiti.httpCon.ApiCom;

import jp.hayamiti.JSON.JACKSONObject;

public class SpRecRes extends JACKSONObject{
	private long sendTime;
	private String result;
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}