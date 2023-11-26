package com.beaker.reciperoulette.ChatRoom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.beaker.reciperoulette.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatRoomLiveView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ChatRoomLiveEntry> entries;
    private ChatRoomWebSocket ws;

    private String name;
    private String details;
    private String contact;
    private boolean isCookingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_live);

        entries = new ArrayList<>();

        recyclerView = findViewById(R.id.goOutList);

        if(recyclerView == null) throw new IllegalStateException();

        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomLiveView.this));
        recyclerView.setAdapter(new ChatRoomLiveAdaptor(ChatRoomLiveView.this, entries));


        //get intent
        Intent i = getIntent();

        //check whether the user selected "cook" or "shop" in EnterChatRoomView
        isCookingRequest = i.getBooleanExtra(getString(R.string.cht_req_type), true);
        name = i.getStringExtra(getString(R.string.cht_req_name));
        details = i.getStringExtra(getString(R.string.cht_req_det));
        contact = i.getStringExtra(getString(R.string.cht_req_cont));

        if(name == null) name = "";
        if(details == null) details = "";
        if(contact == null) contact = "";

        ws = new ChatRoomWebSocket(this, isCookingRequest, name, details, contact);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        int sz = entries.size();
        entries.clear();
        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRangeRemoved(0, sz);
        ws.close();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        ws = new ChatRoomWebSocket(this, isCookingRequest, name, details, contact);
    }


    protected void addItemToList(ChatRoomLiveEntry c) {
        entries.add(0, c);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(0);
    }

    protected void removeItemFromList(ChatRoomLiveEntry c) {
        int index = entries.indexOf(c);
        if (index >= 0) {
            entries.remove(c);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(index);
        }
    }

}
