package jp.hayamiti.httpCon.DbCom;

import java.util.ArrayList;
import java.util.List;

import jp.hayamiti.JSON.JACKSONObject;

public class GetSuggestedHabitRes extends JACKSONObject {
    private List<SuggestedHabit> results = new ArrayList<SuggestedHabit>();
    private boolean success;

    public final List<SuggestedHabit> getResults() {
        return results;
    }

    public final void setResults(List<SuggestedHabit> result) {
        this.results = result;
    }

    public final boolean isSuccess() {
        return success;
    }

    public final void setSuccess(boolean success) {
        this.success = success;
    }
}
