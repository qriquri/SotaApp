package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

final public class PostConditionReq extends JACKSONObject {
	private String nickName;
	private String condition;
	private String sentence;
	private int backDay = 0;
	final public String getNickName() {
		return nickName;
	}
	final public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	final public String getCondition() {
		return condition;
	}
	final public void setCondition(String condition) {
		this.condition = condition;
	}
	final public String getSentence() {
		return sentence;
	}
	final public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	final public int getBackDay() {
		return backDay;
	}
	final public void setBackDay(int backDay) {
		this.backDay = backDay;
	}
}
