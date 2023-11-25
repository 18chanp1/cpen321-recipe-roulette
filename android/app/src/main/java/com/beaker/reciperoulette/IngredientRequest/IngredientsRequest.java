package com.beaker.reciperoulette.IngredientRequest;

import com.beaker.reciperoulette.IngredientRequest.Ingredient;

import java.util.List;

public class IngredientsRequest {
    public String userId;


    public IngredientsRequest(String userId) {
        this.userId = userId;
    }

    public List<Ingredient> ingredients;

    public IngredientsRequest(String userId, List<Ingredient> ingredients) {
        this.userId = userId;
        this.ingredients = ingredients;
    }

    public IngredientsRequest()
    {

    }
}
