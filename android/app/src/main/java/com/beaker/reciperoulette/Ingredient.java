package com.beaker.reciperoulette;

class Ingredient {
    String name;
    int count;
    long[] date;

    public Ingredient()
    {

    }

    public Ingredient(String name, int count, long[] date) {
        this.name = name;
        this.count = count;
        this.date = date;
    }
}
