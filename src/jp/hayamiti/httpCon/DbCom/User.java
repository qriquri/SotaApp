package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

final public class User extends JACKSONObject{
	private String nickName;
	private String furigana;
	private boolean isRegistered;
	final public String getNickName() {
		return nickName;
	}
	final public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	final public String getFurigana() {
		return furigana;
	}
	final public void setFurigana(String furigana) {
		this.furigana = furigana;
	}
	final public boolean getIsRegistered() {
		return isRegistered;
	}
	final public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}



}
