package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomLiveView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ChatRoomLiveEntry> entries;
    private ChatRoomWebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_live);

        entries = new ArrayList<>();

        recyclerView = findViewById(R.id.goOutList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomLiveView.this));
        recyclerView.setAdapter(new ChatRoomLiveAdaptor(getApplicationContext(), entries));


        //get intent
        Intent i = getIntent();
        boolean isCookingRequest = i.getBooleanExtra("COOK", true);
        String name = i.getStringExtra("NAME");
        String details = i.getStringExtra("DETAILS");
        String contact = i.getStringExtra("CONTACT");

        ws = new ChatRoomWebSocket(this, isCookingRequest, name, details, contact);
    }



    protected void addItemToList(ChatRoomLiveEntry c) {
        entries.add(0, c);
        recyclerView.getAdapter().notifyItemInserted(0);
    }

    protected void removeItemFromList(ChatRoomLiveEntry c) {
        int index = entries.indexOf(c);
        if (index >= 0) {
            entries.remove(c);
            recyclerView.getAdapter().notifyItemRemoved(index);
        }
    }

}
