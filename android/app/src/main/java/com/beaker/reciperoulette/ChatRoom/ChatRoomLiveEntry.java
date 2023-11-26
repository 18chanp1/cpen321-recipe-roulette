package com.beaker.reciperoulette.ChatRoom;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents each entry in the food radar
 */
public class ChatRoomLiveEntry implements Parcelable {
    String entryID;
    String name;
    String details;
    String contact;
    String image;
    String type;

    public ChatRoomLiveEntry(String entryID, String name, String details, String contact, String image, String type) {
        this.entryID = entryID;
        this.name = name;
        this.details = details;
        this.contact = contact;
        this.image = image;
        this.type = type;
    }

    protected ChatRoomLiveEntry(Parcel in) {
        entryID = in.readString();
        name = in.readString();
        details = in.readString();
        contact = in.readString();
        image = in.readString();
        type = in.readString();
    }

    public static final Creator<ChatRoomLiveEntry> CREATOR = new Creator<ChatRoomLiveEntry>() {
        @Override
        public ChatRoomLiveEntry createFromParcel(Parcel in) {
            return new ChatRoomLiveEntry(in);
        }

        @Override
        public ChatRoomLiveEntry[] newArray(int size) {
            return new ChatRoomLiveEntry[size];
        }
    };

    @Override
    public int hashCode() {
        return entryID.hashCode();
    }

    @Override
    /*
      Override equals method to only compare the entryID.
     */
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof ChatRoomLiveEntry))
            return false;
        else
        {
            return entryID.equals(((ChatRoomLiveEntry) obj).entryID);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(entryID);
        parcel.writeString(name);
        parcel.writeString(details);
        parcel.writeString(contact);
        parcel.writeString(image);
        parcel.writeString(type);
    }
}
