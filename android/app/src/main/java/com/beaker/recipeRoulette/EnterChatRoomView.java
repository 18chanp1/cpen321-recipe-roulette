package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterChatRoomView extends AppCompatActivity {

    EditText nameEntry;
    EditText detailEntry;
    EditText contactEntry;
    Button goCookBut;
    Button goShopBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_chat_room_view);

        nameEntry = findViewById(R.id.ecc_name);
        detailEntry = findViewById(R.id.ecc_details);
        contactEntry = findViewById(R.id.ecc_contact);
        goCookBut = findViewById(R.id.ecc_gocook_but);
        goShopBut = findViewById(R.id.ecc_goshop_but);

        goCookBut.setOnClickListener(view -> {
            Intent i = new Intent(EnterChatRoomView.this, ChatRoomLiveView.class);
            i.putExtra("NAME", nameEntry.getText().toString());
            i.putExtra("DETAILS", detailEntry.getText().toString());
            i.putExtra("CONTACT", contactEntry.getText().toString());
            i.putExtra("COOK", true);

            startActivity(i);
        });

        goShopBut.setOnClickListener(view -> {
            Intent i = new Intent(EnterChatRoomView.this, ChatRoomLiveView.class);
            i.putExtra("NAME", nameEntry.getText().toString());
            i.putExtra("DETAILS", detailEntry.getText().toString());
            i.putExtra("CONTACT", contactEntry.getText().toString());
            i.putExtra("COOK", false);

            startActivity(i);
        });
    }
}