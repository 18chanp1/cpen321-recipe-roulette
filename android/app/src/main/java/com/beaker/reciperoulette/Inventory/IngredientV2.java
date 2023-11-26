package com.beaker.reciperoulette.Inventory;
import com.beaker.reciperoulette.IngredientRequest.Ingredient;

public class IngredientV2 {
    public String name;
    public int count;
    public String[] date;

    public IngredientV2() {
    }
    public IngredientV2(String name, int count, String[] date) {
        this.name = name;
        this.count = count;
        this.date = date;
    }
}