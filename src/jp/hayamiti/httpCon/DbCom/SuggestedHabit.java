package jp.hayamiti.httpCon.DbCom;

import jp.hayamiti.JSON.JACKSONObject;

public class SuggestedHabit extends JACKSONObject {
    private long id;
    private String nickName;
    private long date;
    private int item;
    private int value;
    private int year;
    private int month;
    private int day;
    public final long getId() {
        return id;
    }
    public final void setId(long id) {
        this.id = id;
    }
    public final String getNickName() {
        return nickName;
    }
    public final void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public final long getDate() {
        return date;
    }
    public final void setDate(long date) {
        this.date = date;
    }
    public final int getItem() {
        return item;
    }
    public final void setItem(int item) {
        this.item = item;
    }
    public final int getValue() {
        return value;
    }
    public final void setValue(int value) {
        this.value = value;
    }
    public final int getYear() {
        return year;
    }
    public final void setYear(int year) {
        this.year = year;
    }
    public final int getMonth() {
        return month;
    }
    public final void setMonth(int month) {
        this.month = month;
    }
    public final int getDay() {
        return day;
    }
    public final void setDay(int day) {
        this.day = day;
    }

}
