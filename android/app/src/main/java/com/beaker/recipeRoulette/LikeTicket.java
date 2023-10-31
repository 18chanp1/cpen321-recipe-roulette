package com.beaker.recipeRoulette;

public class LikeTicket {
    private String userToken;
    private int id;
    boolean like;
    String email;

    public LikeTicket(String userToken, int id, boolean like, String email) {
        this.userToken = userToken;
        this.id = id;
        this.like = like;
        this.email = email;
    }
}
