package jp.hayamiti.httpCon;

final public class LifeHabit {
	private String sleepT = "";
	private String getUpT = "";
	private String exerciseT = "";
	private String drinkingT = "";
	private String eatBreakfastT = "";
	private String eatSnackT = "";
	private String snackNameT = "";

	private int sleep = 0;
	private int getUp = 0;
	private boolean exercise = false;
	private boolean drinking = false;
	private boolean eatBreakfast = false;
	private boolean eatSnack = false;
	private String snackName = "";
	final public void setText(String sleepT, String getUpT, String exerciseT, String drinkingT, String eatBreakfastT, String eatSnackT, String snackNameT) {
		this.sleepT = sleepT;
		this.getUpT = getUpT;
		this.exerciseT = exerciseT;
		this.drinkingT = drinkingT;
		this.eatBreakfastT = eatBreakfastT;
		this.eatSnackT = eatSnackT;
		this.snackNameT = snackNameT;
	};

	final public void setVal(int sleep, int getUp, boolean exercise, boolean drinking, boolean eatBreakfast, boolean eatSnack, String snackName) {
		this.sleep = sleep;
		this.getUp = getUp;
		this.exercise = exercise;
		this.drinking = drinking;
		this.eatBreakfast = eatBreakfast;
		this.eatSnack = eatSnack;
		this.snackName = snackName;
	};

	final public String[] getTextList() {
		String[] list = {sleepT, getUpT, exerciseT, drinkingT, eatBreakfastT, eatSnackT, snackNameT};
		return list;
	};

	final public int getSleep() {
		return sleep;
	}
	final public int getGetUp() {
		return getUp;
	}
	final public boolean getExercise() {
		return exercise;
	}
	final public boolean getDrinking() {
		return drinking;
	}
	final public boolean getEatBreakfast() {
		return eatBreakfast;
	}
	final public boolean getEatSnack() {
		return eatSnack;
	}
	final public String getSnackName() {
		return snackName;
	}
}
