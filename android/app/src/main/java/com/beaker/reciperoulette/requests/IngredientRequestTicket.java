package com.beaker.reciperoulette.requests;

public class IngredientRequestTicket {
    String fcmtok;
    String userToken;
    String requestItem;
    String phoneNo;
    String email;

    public IngredientRequestTicket(String userToken, String requestItem, String phoneNo, String fcmtok, String email) {
        this.userToken = userToken;
        this.requestItem = requestItem;
        this.phoneNo = phoneNo;
        this.email = email;
        this.fcmtok = fcmtok;
    }

}
