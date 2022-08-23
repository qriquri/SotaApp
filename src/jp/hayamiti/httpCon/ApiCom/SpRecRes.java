package jp.hayamiti.httpCon.ApiCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

final public class SpRecRes extends JACKSONObject{
	private long sendTime;
	private String result;
	private List<String> alternative = new ArrayList<String>();
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
	final public List<String> getAlternative() {
		return alternative;
	}
	final public void setAlternative(List<String> alternative) {
		this.alternative = alternative;
	}
}