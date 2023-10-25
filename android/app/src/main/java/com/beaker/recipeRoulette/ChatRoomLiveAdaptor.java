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
        holder.detailedView.setText("Contact: " + item.contact);

        if (item.type.equals("SHOPREQ"))
        {
            holder.imageView.setImageResource(R.drawable.shopping_cart);
        }
        else if (item.type.equals("COOKREQ"))
        {
            holder.imageView.setImageResource(R.drawable.frying_pan);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
