package com.beaker.reciperoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IngredientRequestView extends AppCompatActivity {
    //private Button requestIngredientButton;
    //private Button viewRequestButton; //Put this for reference to ensure that we know all buttons used.
    private EditText ingredientRequestText;
    private EditText phoneNumberText;
    private List<IngredientRequest> ingredientRequests;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_req_view);

        //setup buttons
        Button requestIngredientButton = findViewById(R.id.rq_ingredreq_but);
        Button viewRequestButton = findViewById(R.id.rq_selfreq_but);
        ingredientRequestText = findViewById(R.id.rq_ingredient_entry);
        phoneNumberText = findViewById(R.id.rq_contact_entry);

        loadDonations();

        //setup the listener to submit requests

        requestIngredientButton.setOnClickListener(view -> {
            submitRequestHandler();
        });

        viewRequestButton.setOnClickListener(view ->
        {
            Intent selfReqI = new Intent(IngredientRequestView.this, IngredientRequestSelfView.class);
            startActivity(selfReqI);
        });
    }

    private void submitRequestHandler()
    {

        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");
        String fcmtok = sharedPref.getString("FCMTOKEN","NOTOKEN");


        OkHttpClient client = new OkHttpClient();

        String foodReq = String.valueOf(ingredientRequestText.getText());
        String phoneNo = String.valueOf(phoneNumberText.getText());
        Gson gson = new Gson();
        String json = gson.toJson(new IngredientRequestTicket(tok, foodReq, phoneNo, fcmtok, email));

        //do not allow blank submissions
        if(foodReq.length() <= 0 || phoneNo.length() <=0)
        {
            Toast toast = Toast.makeText(IngredientRequestView.this,
                    getText(R.string.must_enter_contact), Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request postNewReq = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests/new")
                .addHeader("userToken", tok)
                .addHeader("requestItem", foodReq)
                .addHeader("phoneNo", phoneNo)
                .addHeader("email", email)
                .addHeader("fcmTok", fcmtok)
                .post(body)
                .build();

        client.newCall(postNewReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                IngredientRequestView.this.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(IngredientRequestView.this, "Request failed", Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                IngredientRequestView.this.runOnUiThread(() -> {
                    if (response.code() == 511)
                    {

                        CharSequence s = "Exit the app and try again";

                        Toast t = Toast.makeText(IngredientRequestView.this, s, Toast.LENGTH_SHORT);
                        t.show();

                    }
                    else if(response.isSuccessful()) {
                        Log.d("test", "call ok");
                        Toast toast = Toast.makeText(IngredientRequestView.this, "Request made", Toast.LENGTH_LONG);
                        toast.show();

                        phoneNumberText.setText("");
                        ingredientRequestText.setText("");

                        //reload donations list
                        loadDonations();
                    }
                });
            }
        });
    }

    public void loadDonations()
    {
        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 511)
                {

                    CharSequence s = "Exit the app and try again";

                    Toast t = Toast.makeText(IngredientRequestView.this, s, Toast.LENGTH_SHORT);
                    t.show();
                }
                else if(response.isSuccessful())
                {
                    String res = response.body().string();

                    IngredientRequestView.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //TODO add expiry logic
                            IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                            ingredientRequests = new ArrayList<IngredientRequest>();

                            for(IngredientRequest r : userArray)
                            {
                                ingredientRequests.add(r);
                            }

                            recyclerView = findViewById(R.id.rq_recycler);
                            recyclerView.setLayoutManager(new LinearLayoutManager(IngredientRequestView.this));
                            recyclerView.setAdapter(new IngredientRequestAdaptor(getApplicationContext(), IngredientRequestView.this, ingredientRequests));

                        }
                    });


                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent i = new Intent(this, MainMenu.class);
            startActivity(i);
            return true;
        }
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
            submitRequestHandler();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}