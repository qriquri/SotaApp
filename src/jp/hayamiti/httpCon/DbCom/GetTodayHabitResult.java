package jp.hayamiti.httpCon.DbCom;

public class GetTodayHabitResult{
	private String nickName;
	private long date;
	private int sleep;
	private int getUp;
	private boolean exercise;
	private boolean drinking;
	private boolean eatBreakfast;
	private boolean eatSnack;
	private String snackName;
	private int year;
	private int month;
	private int day;
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public int getSleep() {
		return sleep;
	}
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	public int getGetUp() {
		return getUp;
	}
	public void setGetUp(int getUp) {
		this.getUp = getUp;
	}
	public boolean isExercise() {
		return exercise;
	}
	public void setExercise(boolean exercise) {
		this.exercise = exercise;
	}
	public boolean isDrinking() {
		return drinking;
	}
	public void setDrinking(boolean drinking) {
		this.drinking = drinking;
	}
	public boolean isEatBreakfast() {
		return eatBreakfast;
	}
	public void setEatBreakfast(boolean eatBreakfast) {
		this.eatBreakfast = eatBreakfast;
	}
	public boolean isEatSnack() {
		return eatSnack;
	}
	public void setEatSnack(boolean eatSnack) {
		this.eatSnack = eatSnack;
	}
	public String getSnackName() {
		return snackName;
	}
	public void setSnackName(String snackName) {
		this.snackName = snackName;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
}
