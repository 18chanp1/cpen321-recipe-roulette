package com.beaker.reciperoulette.requests;

public class Ingredient {
    public String name;
    public int count;
    public long[] date;

    public Ingredient()
    {

    }

    public Ingredient(String name, int count, long[] date) {
        this.name = name;
        this.count = count;
        this.date = date;
    }
}
