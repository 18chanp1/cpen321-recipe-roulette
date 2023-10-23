package com.beaker.recipeRoulette;

public class IngredientRequestTicket {
    String tok;
    String ingredient;
    String phoneNo;

    public IngredientRequestTicket(String tok, String ingredient, String phoneNo) {
        this.tok = tok;
        this.ingredient = ingredient;
        this.phoneNo = phoneNo;
    }
}
