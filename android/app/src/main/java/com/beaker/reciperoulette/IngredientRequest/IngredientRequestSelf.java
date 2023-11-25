package com.beaker.reciperoulette.IngredientRequest;

import android.os.Parcel;

public class IngredientRequestSelf extends IngredientRequest{


    String donationDetails;

    public IngredientRequestSelf(String reqID, String reqDate, String expiryDate, String ingredientID, String ingredientName, String requestor, String requestorID, String image) {
        super(reqID, reqDate, expiryDate, ingredientID, ingredientName, requestor, requestorID, image);
    }


    protected IngredientRequestSelf(Parcel in) {
        super(in);
    }

    public IngredientRequestSelf(String reqID, String reqDate, String expiryDate, String ingredientID, String ingredientName, String requestor, String requestorID, String image, String donationDetails) {
        super(reqID, reqDate, expiryDate, ingredientID, ingredientName, requestor, requestorID, image);
        this.donationDetails = donationDetails;
    }

    public IngredientRequestSelf(Parcel in, String donationDetails) {
        super(in);
        this.donationDetails = donationDetails;
    }


    public String getDonationDetails() {
        return donationDetails;
    }

    public void setDonationDetails(String donationDetails) {
        this.donationDetails = donationDetails;
    }
}
