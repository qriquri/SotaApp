package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class PostConditionReq extends JACKSONObject {
	private String nickName;
	private String condition;
	private String sentence;
	private int backDay = 0;
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public int getBackDay() {
		return backDay;
	}
	public void setBackDay(int backDay) {
		this.backDay = backDay;
	}
}
