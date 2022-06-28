package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class User extends JACKSONObject{
	private String nickName;
	private String furigana;
	private boolean isRegistered;
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getFurigana() {
		return furigana;
	}
	public void setFurigana(String furigana) {
		this.furigana = furigana;
	}
	public boolean getIsRegistered() {
		return isRegistered;
	}
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}



}
