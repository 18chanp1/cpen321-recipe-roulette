package com.beaker.reciperoulette.reviews;public class CustomReview {
    String userId;
    String recipeName;
    String recipeId;
    String recipeSummary;

    public CustomReview(String userId, String recipeName, String recipeSummary) {
        this.userId = userId;
        this.recipeName = recipeName;
        this.recipeSummary = recipeSummary;
    }
}
