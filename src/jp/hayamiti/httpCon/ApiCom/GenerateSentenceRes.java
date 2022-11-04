package jp.hayamiti.httpCon.ApiCom;

import java.util.ArrayList;

import jp.hayamiti.JSON.JACKSONObject;

public class GenerateSentenceRes extends JACKSONObject {
    private long sendTime;
    private ArrayList<String> result;
    public long getSendTime() {
        return sendTime;
    }
    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
    public ArrayList<String> getResult() {
        return result;
    }
    public void setResult(ArrayList<String> result) {
        this.result = result;
    }
}
