package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class GetTodayHabitRes extends JACKSONObject{
	public class GetTodayHabitResult{
		public String nickName;
		public long date;
		public int sleep;
		public int getUp;
		public boolean exercise;
		public boolean drinking;
		public boolean eatBreakfast;
		public boolean eatSnack;
		public String snackName;
		public int year;
		public int month;
		public int day;
	}
	public GetTodayHabitResult result;
	public boolean success;
}
