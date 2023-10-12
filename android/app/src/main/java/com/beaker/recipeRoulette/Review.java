package com.beaker.recipeRoulette;

import android.media.Image;

import java.util.Calendar;

public class Review {
    int ID;
    int rating;
    String author;
    String title;
    String review;
    Calendar publishDate;
    int image;

    public Review(int ID, int rating, String author, String title, String review, Calendar publishDate, int image) {
        this.ID = ID;
        this.rating = rating;
        this.author = author;
        this.title = title;
        this.review = review;
        this.publishDate = publishDate;
        this.image = image;
    }

    public int getImage() {
        return image;
    }



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Calendar getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Calendar publishDate) {
        this.publishDate = publishDate;
    }
}
