package com.beaker.reciperoulette.inventory;


public class IngredientRequestResult {
    String _id;
    String userId;
    IngredientV2[] ingredients;

    public IngredientRequestResult(String _id, String userId, IngredientV2[] ingredients) {
        this._id = _id;
        this.userId = userId;
        this.ingredients = ingredients;
    }
}
