package jp.hayamiti.httpCon;

public class TestJSON {
	public int id;
	public String name;

	 @Override // これたぶん普通のtoStringをオーバライドしてるだけ
	public String toString() {
		return "TestJson [id="+id+",name="+name+"}";
	}
}
