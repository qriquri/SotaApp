package jp.hayamiti.httpCon.DbResponse;

import java.util.ArrayList;
import java.util.List;

public class GetUserNamesRes{
	public List<User> users = new ArrayList<User>(); // 配列だけインナークラスはダメだった
	public boolean err;
}
