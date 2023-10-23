package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatRoom extends AppCompatActivity {
    private ChatRoomWebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ws = new ChatRoomWebSocket(this);
        ws.sendAString("MSG", "TEST1");
    }

    //TODO close when done
}