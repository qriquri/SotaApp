package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class GetUserNamesRes extends JACKSONObject{
	public List<User> users = new ArrayList<User>(); // 配列だけインナークラスはダメだった
	public boolean err;
}
