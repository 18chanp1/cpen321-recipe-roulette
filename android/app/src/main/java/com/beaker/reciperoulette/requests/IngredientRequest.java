package com.beaker.reciperoulette.requests;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class IngredientRequest implements Parcelable {
    String reqID;
    String reqDate;
    String expiryDate;
    String ingredientID;
    String ingredientName;
    String requester;
    String userId; //the requester ID

    String image;
    public IngredientRequest(String reqID, String reqDate, String expiryDate, String ingredientID, String ingredientName, String requester, String userId, String image) {
        this.reqID = reqID;
        this.reqDate = reqDate;
        this.expiryDate = expiryDate;
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.requester = requester;
        this.userId = userId;
        this.image = image;
    }

    public IngredientRequest(String ingredientName, String userId)
    {
        this.ingredientName = ingredientName;
        this.userId = userId;
    }

    protected IngredientRequest(Parcel in) {
        reqID = in.readString();
        reqDate = in.readString();
        expiryDate = in.readString();
        ingredientID = in.readString();
        ingredientName = in.readString();
        requester = in.readString();
        userId = in.readString();
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

    public String getIngredientName() {
        return ingredientName;
    }

    public String getUserId() {
        return userId;
    }

    public String getImage() {
        return image;
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
        parcel.writeString(requester);
        parcel.writeString(userId);
        parcel.writeString(image);
    }

}
