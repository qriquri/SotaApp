package jp.hayamiti.httpCon.DbCom;

final public class GetTodayHabitResult{
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
	final public String getNickName() {
		return nickName;
	}
	final public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	final public long getDate() {
		return date;
	}
	final public void setDate(long date) {
		this.date = date;
	}
	final public int getSleep() {
		return sleep;
	}
	final public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	final public int getGetUp() {
		return getUp;
	}
	final public void setGetUp(int getUp) {
		this.getUp = getUp;
	}
	final public boolean isExercise() {
		return exercise;
	}
	final public void setExercise(boolean exercise) {
		this.exercise = exercise;
	}
	final public boolean isDrinking() {
		return drinking;
	}
	final public void setDrinking(boolean drinking) {
		this.drinking = drinking;
	}
	final public boolean isEatBreakfast() {
		return eatBreakfast;
	}
	final public void setEatBreakfast(boolean eatBreakfast) {
		this.eatBreakfast = eatBreakfast;
	}
	final public boolean isEatSnack() {
		return eatSnack;
	}
	final public void setEatSnack(boolean eatSnack) {
		this.eatSnack = eatSnack;
	}
	final public String getSnackName() {
		return snackName;
	}
	final public void setSnackName(String snackName) {
		this.snackName = snackName;
	}
	final public int getYear() {
		return year;
	}
	final public void setYear(int year) {
		this.year = year;
	}
	final public int getMonth() {
		return month;
	}
	final public void setMonth(int month) {
		this.month = month;
	}
	final public int getDay() {
		return day;
	}
	final public void setDay(int day) {
		this.day = day;
	}
}
