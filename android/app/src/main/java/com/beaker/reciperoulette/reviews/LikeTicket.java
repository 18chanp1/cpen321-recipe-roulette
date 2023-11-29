package com.beaker.reciperoulette.reviews;

public class LikeTicket {
    String userToken;
    String id;
    boolean like;
    String email;

    public LikeTicket(String userToken, String id, boolean like, String email) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
