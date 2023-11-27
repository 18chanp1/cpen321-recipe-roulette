package com.beaker.reciperoulette.requests;

import java.util.List;

public class IngredientsRequest {
    public String userId;
    public List<Ingredient> ingredients;
    public IngredientsRequest(String userId) {
        this.userId = userId;
    }


    public IngredientsRequest()
    {
    }
}
