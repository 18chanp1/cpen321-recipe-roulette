package com.beaker.reciperoulette.ChatRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

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

        boolean nameValid = item.name != null;
        boolean contactValid = item.contact != null;
        boolean typeValid = item.type != null;

        if(!nameValid || !contactValid || typeValid)
        {
            return;
        }


        holder.nameView.setText(item.name);

        String displayedContact = context.getString(R.string.cht_contact_head) + item.contact;
        holder.detailedView.setText(displayedContact);

        String shopreqType = context.getString(R.string.cht_type_shopreq);
        String cookreqType = context.getString(R.string.cht_type_cookreq);
        if (item.type.equals(shopreqType))
        {
            holder.imageView.setImageResource(R.drawable.shopping_cart);
        }
        else if (item.type.equals(cookreqType))
        {
            holder.imageView.setImageResource(R.drawable.frying_pan);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
