package jp.hayamiti.httpCon.ApiCom;

import jp.hayamiti.JSON.JACKSONObject;

public class GenerateSentenceReq extends JACKSONObject {
    private String startSentence;

    public String getStartSentence() {
        return startSentence;
    }

    public void setStartSentence(String startSentence) {
        this.startSentence = startSentence;
    }
}
