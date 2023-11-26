package com.beaker.reciperoulette.Inventory;public class DeleteTicket {
    String userId;
    String[] ingredients;

    public DeleteTicket(String userId, String[] ingredients) {
        this.userId = userId;
        this.ingredients = ingredients;
    }
}
