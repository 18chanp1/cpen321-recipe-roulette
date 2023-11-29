package com.beaker.reciperoulette.requests;

public class IngredientRequestAcceptTicket {
    String userToken;
    String requestId;
    String email;

    public IngredientRequestAcceptTicket(String userToken, String requestId, String email) {
        this.userToken = userToken;
        this.requestId = requestId;
        this.email = email;
    }

}
