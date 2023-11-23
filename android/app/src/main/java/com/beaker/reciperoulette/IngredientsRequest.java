package com.beaker.reciperoulette;

import java.util.List;

public class IngredientsRequest {
    String userId;


    public IngredientsRequest(String userId) {
        this.userId = userId;
    }

    List<Ingredient> ingredients;

    public IngredientsRequest(String userId, List<Ingredient> ingredients) {
        this.userId = userId;
        this.ingredients = ingredients;
    }

    public IngredientsRequest()
    {

    }
}
