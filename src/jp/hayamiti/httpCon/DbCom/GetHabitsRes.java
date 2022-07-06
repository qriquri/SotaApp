package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class GetHabitsRes extends JACKSONObject {
	private List<GetTodayHabitResult> results = new ArrayList<GetTodayHabitResult>();
	private boolean success;
	public List<GetTodayHabitResult> getResults() {
		return results;
	}
	public void setResults(List<GetTodayHabitResult> results) {
		this.results = results;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
