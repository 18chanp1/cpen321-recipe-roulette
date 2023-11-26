package com.beaker.reciperoulette.ChatRoom;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class ChatRoomLiveHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    TextView detailedView;
    Button Details;

    public ChatRoomLiveHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = itemView.findViewById(R.id.imageview);
        this.nameView = itemView.findViewById(R.id.name);
        this.detailedView = itemView.findViewById(R.id.email);
        Details = itemView.findViewById(R.id.readmore_but);
    }

}
