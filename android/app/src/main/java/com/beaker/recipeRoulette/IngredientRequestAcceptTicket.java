package com.beaker.recipeRoulette;

public class IngredientRequestAcceptTicket {
    String userToken;
    String reqID;
    String email;

    public IngredientRequestAcceptTicket(String userToken, String reqID, String email) {
        this.userToken = userToken;
        this.reqID = reqID;
        this.email = email;
    }

}
