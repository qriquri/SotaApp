package jp.hayamiti.httpCon.ApiCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class YesOrNoReq extends JACKSONObject {
	private List<String> alternative = new ArrayList<String>();

	public List<String> getAlternative() {
		return alternative;
	}

	public void setAlternative(List<String> alternative) {
		this.alternative = alternative;
	}
}
