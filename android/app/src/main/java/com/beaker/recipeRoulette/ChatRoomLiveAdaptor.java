package com.beaker.recipeRoulette;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatRoomLiveAdaptor extends RecyclerView.Adapter<ChatRoomLiveHolder> {

    Context context;
    List<ChatRoomLiveEntry> items;
    public ChatRoomLiveAdaptor(Context c, List<ChatRoomLiveEntry> items) {
        this.context = c;
        this.items = items;
    }

    @NonNull
    @Override
    public ChatRoomLiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomLiveHolder(LayoutInflater.from(context).inflate(R.layout.chat_room_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomLiveHolder holder, int position) {
        ChatRoomLiveEntry item = items.get(position);

        holder.nameView.setText(item.name);
        holder.detailedView.setText(item.details);

        String url = item.image;
        Picasso.with(this.context).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
