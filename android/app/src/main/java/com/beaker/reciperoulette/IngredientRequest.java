package com.beaker.reciperoulette;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class IngredientRequest implements Parcelable {
    String reqID;
    String reqDate;
    String expiryDate;
    String ingredientID;
    String ingredientName;
    String requestor;
    String requestorID;

    String image;
    public IngredientRequest(String reqID, String reqDate, String expiryDate, String ingredientID, String ingredientName, String requestor, String requestorID, String image) {
        this.reqID = reqID;
        this.reqDate = reqDate;
        this.expiryDate = expiryDate;
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.requestor = requestor;
        this.requestorID = requestorID;
        this.image = image;
    }

    protected IngredientRequest(Parcel in) {
        reqID = in.readString();
        reqDate = in.readString();
        expiryDate = in.readString();
        ingredientID = in.readString();
        ingredientName = in.readString();
        requestor = in.readString();
        requestorID = in.readString();
        image = in.readString();
    }

    public static final Creator<IngredientRequest> CREATOR = new Creator<IngredientRequest>() {
        @Override
        public IngredientRequest createFromParcel(Parcel in) {
            return new IngredientRequest(in);
        }

        @Override
        public IngredientRequest[] newArray(int size) {
            return new IngredientRequest[size];
        }
    };

    public String getReqID() {
        return reqID;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(String ingredientID) {
        this.ingredientID = ingredientID;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getRequestorID() {
        return requestorID;
    }

    public void setRequestorID(String requestorID) {
        this.requestorID = requestorID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

        parcel.writeString(reqID);
        parcel.writeString(reqDate);
        parcel.writeString(expiryDate);
        parcel.writeString(ingredientID);
        parcel.writeString(ingredientName);
        parcel.writeString(requestor);
        parcel.writeString(requestorID);
        parcel.writeString(image);
    }

}
