package jp.hayamiti.httpCon.DbCom;

final public class PostHabitReq {
	private String nickName;
	private int sleep;
	private int getUp;
	private boolean exercise;
	private boolean drinking;
	private boolean eatBreakfast;
	private boolean eatSnack;
	private String snackName;
	private String sleepT;
	private String getUpT;
	private String exerciseT;
	private String drinkingT;
	private String eatBreakfastT;
	private String eatSnackT;
	private String snackNameT;
	private int backDay = 0;
	final public String getNickName() {
		return nickName;
	}
	final public void setNickName(String nickName) {
		this.nickName = nickName;
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
	final public String getSleepT() {
		return sleepT;
	}
	final public void setSleepT(String sleepT) {
		this.sleepT = sleepT;
	}
	final public String getGetUpT() {
		return getUpT;
	}
	final public void setGetUpT(String getUpT) {
		this.getUpT = getUpT;
	}
	final public String getExerciseT() {
		return exerciseT;
	}
	final public void setExerciseT(String exerciseT) {
		this.exerciseT = exerciseT;
	}
	final public String getDrinkingT() {
		return drinkingT;
	}
	final public void setDrinkingT(String drinkingT) {
		this.drinkingT = drinkingT;
	}
	final public String getEatBreakfastT() {
		return eatBreakfastT;
	}
	final public void setEatBreakfastT(String eatBreakfastT) {
		this.eatBreakfastT = eatBreakfastT;
	}
	final public String getEatSnackT() {
		return eatSnackT;
	}
	final public void setEatSnackT(String eatSnackT) {
		this.eatSnackT = eatSnackT;
	}
	final public String getSnackNameT() {
		return snackNameT;
	}
	final public void setSnackNameT(String snackNameT) {
		this.snackNameT = snackNameT;
	}
	final public int getBackDay() {
		return backDay;
	}
	final public void setBackDay(int backDay) {
		this.backDay = backDay;
	}
}
