package jp.hayamiti.httpCon.ApiCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

final public class YesOrNoReq extends JACKSONObject {
	private List<String> alternative = new ArrayList<String>();

	final public List<String> getAlternative() {
		return alternative;
	}

	final public void setAlternative(List<String> alternative) {
		this.alternative = alternative;
	}
}
