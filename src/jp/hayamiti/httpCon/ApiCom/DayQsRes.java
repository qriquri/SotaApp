package jp.hayamiti.httpCon.ApiCom;

import jp.hayamiti.JSON.JACKSONObject;

public class DayQsRes extends JACKSONObject {
	private long sendTime;
	private String result;
	private String text;
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}