package com.beaker.recipeRoulette;

public class LikeTicket {
    private String usertoken;
    private int id;
    boolean like;

    public LikeTicket(String usertoken, int id, boolean like) {
        this.usertoken = usertoken;
        this.id = id;
        this.like = like;
    }
}
