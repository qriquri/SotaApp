package jp.hayamiti.httpCon;

public class LifeHabit {
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
	public void setText(String sleepT, String getUpT, String exerciseT, String drinkingT, String eatBreakfastT, String eatSnackT, String snackNameT) {
		this.sleepT = sleepT;
		this.getUpT = getUpT;
		this.exerciseT = exerciseT;
		this.drinkingT = drinkingT;
		this.eatBreakfastT = eatBreakfastT;
		this.eatSnackT = eatSnackT;
		this.snackNameT = snackNameT;
	};

	public void setVal(int sleep, int getUp, boolean exercise, boolean drinking, boolean eatBreakfast, boolean eatSnack, String snackName) {
		this.sleep = sleep;
		this.getUp = getUp;
		this.exercise = exercise;
		this.drinking = drinking;
		this.eatBreakfast = eatBreakfast;
		this.eatSnack = eatSnack;
		this.snackName = snackName;
	};

	public String[] getTextList() {
		String[] list = {sleepT, getUpT, exerciseT, drinkingT, eatBreakfastT, eatSnackT, snackNameT};
		return list;
	};
	
	public int getSleep() {
		return sleep;
	}
	public int getGetUp() {
		return getUp;
	}
	public boolean getExercise() {
		return exercise;
	}
	public boolean getDrinking() {
		return drinking;
	}
	public boolean getEatBreakfast() {
		return eatBreakfast;
	}
	public boolean getEatSnack() {
		return eatSnack;
	}
	public String getSnackName() {
		return snackName;
	}
}
