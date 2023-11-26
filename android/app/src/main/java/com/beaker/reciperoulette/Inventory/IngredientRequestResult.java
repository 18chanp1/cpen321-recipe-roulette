package com.beaker.reciperoulette.Inventory;


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
