package jp.hayamiti.httpCon.ApiCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class SpRecRes extends JACKSONObject{
	private long sendTime;
	private String result;
	private List<String> alternative = new ArrayList<String>();
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
	public List<String> getAlternative() {
		return alternative;
	}
	public void setAlternative(List<String> alternative) {
		this.alternative = alternative;
	}
}