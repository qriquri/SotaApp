package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

final public class GetHabitsRes extends JACKSONObject {
	private List<GetTodayHabitResult> results = new ArrayList<GetTodayHabitResult>();
	private boolean success;
	final public List<GetTodayHabitResult> getResults() {
		return results;
	}
	final public void setResults(List<GetTodayHabitResult> results) {
		this.results = results;
	}
	final public boolean isSuccess() {
		return success;
	}
	final public void setSuccess(boolean success) {
		this.success = success;
	}
}
