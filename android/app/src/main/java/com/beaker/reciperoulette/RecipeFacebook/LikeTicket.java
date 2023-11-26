package com.beaker.reciperoulette.RecipeFacebook;

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

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
