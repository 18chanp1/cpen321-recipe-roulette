package com.beaker.reciperoulette.IngredientRequest;

import java.util.List;

public class IngredientsRequest {
    public String userId;
    public IngredientsRequest(String userId) {
        this.userId = userId;
    }
    public List<Ingredient> ingredients;

    public IngredientsRequest()
    {
    }
}
