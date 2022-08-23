package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

final public class GetUserNamesRes extends JACKSONObject{
	private List<User> users = new ArrayList<User>(); // 配列だけインナークラスはダメだった
	private boolean err;
	final public List<User> getUsers() {
		return users;
	}
	final public void setUsers(List<User> users) {
		this.users = users;
	}
	final public boolean isErr() {
		return err;
	}
	final public void setErr(boolean err) {
		this.err = err;
	}
}
