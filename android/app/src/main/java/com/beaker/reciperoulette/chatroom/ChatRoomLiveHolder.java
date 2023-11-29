package com.beaker.reciperoulette.chatroom;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class ChatRoomLiveHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    TextView detailedView;

    public ChatRoomLiveHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = itemView.findViewById(R.id.img);
        this.nameView = itemView.findViewById(R.id.item);
        this.detailedView = itemView.findViewById(R.id.expiry);
    }

}
