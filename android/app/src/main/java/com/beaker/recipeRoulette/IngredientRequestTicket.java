package com.beaker.recipeRoulette;

public class IngredientRequestTicket {
    String tok;
    String fcmtok;
    String ingredient;
    String phoneNo;

    public IngredientRequestTicket(String tok, String fcmtok, String ingredient, String phoneNo) {
        this.tok = tok;
        this.fcmtok = fcmtok;
        this.ingredient = ingredient;
        this.phoneNo = phoneNo;
    }

}
