package jp.hayamiti.httpCon.ApiCom;

import jp.hayamiti.JSON.JACKSONObject;

final public class HabitQsRes extends JACKSONObject{
	private long sendTime;
	private String result;
	private String text;
	final public long getSendTime() {
		return sendTime;
	}
	final public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	final public String getResult() {
		return result;
	}
	final public void setResult(String result) {
		this.result = result;
	}
	final public String getText() {
		return text;
	}
	final public void setText(String text) {
		this.text = text;
	}
}