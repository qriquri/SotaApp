package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class GetUserNamesRes extends JACKSONObject{
	private List<User> users = new ArrayList<User>(); // 配列だけインナークラスはダメだった
	private boolean err;
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public boolean isErr() {
		return err;
	}
	public void setErr(boolean err) {
		this.err = err;
	}
}
