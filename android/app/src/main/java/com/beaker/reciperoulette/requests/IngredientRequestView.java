package com.beaker.reciperoulette.requests;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.Utilities;
import com.beaker.reciperoulette.MainMenu;
import com.beaker.reciperoulette.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IngredientRequestView extends AppCompatActivity {
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

        if(requestIngredientButton == null ||
                viewRequestButton == null ||
                ingredientRequestText == null ||
                phoneNumberText == null)
            throw new IllegalStateException();

        loadDonations();

        //setup the listener to submit requests

        requestIngredientButton.setOnClickListener(view -> submitRequestHandler());
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
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));
        String email = sharedPref.getString(getString(R.string.prf_eml), getString(R.string.prf_eml_def));
        String fcmtok = sharedPref.getString(getString(R.string.prf_fcmtoken),getString(R.string.prf_token_def));

        if(tok.equals(getString(R.string.prf_token_def)) ||
            email.equals(getString(R.string.prf_eml_def)) ||
            fcmtok.equals(getString(R.string.prf_token_def)))
            throw new IllegalStateException();

        OkHttpClient client = new OkHttpClient();

        String foodReq = String.valueOf(ingredientRequestText.getText());
        String phoneNo = String.valueOf(phoneNumberText.getText());

        Gson gson = new Gson();
        String json = gson.toJson(new IngredientRequestTicket(tok, foodReq, phoneNo, fcmtok, email));

        //do not allow blank submissions
        if(foodReq.length() == 0 || phoneNo.length() == 0)
        {
            Toast toast = Toast.makeText(IngredientRequestView.this,
                    getText(R.string.must_enter_contact), Toast.LENGTH_LONG);
            toast.show();
            return;
        }


        MediaType JSON = MediaType.get(getString(R.string.http_json_type));
        RequestBody body = RequestBody.create(json, JSON);

        Request postNewReq = new Request.Builder()
                .url(getString(R.string.http_ingred_req_new_url))
                .addHeader(getString(R.string.http_args_userToken), tok)
                .addHeader(getString(R.string.http_args_requestItem), foodReq)
                .addHeader(getString(R.string.http_args_phoneno), phoneNo)
                .addHeader(getString(R.string.http_args_email), email)
                .addHeader(getString(R.string.http_args_fcmtok), fcmtok)
                .post(body)
                .build();

        client.newCall(postNewReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                IngredientRequestView.this.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(IngredientRequestView.this, getString(R.string.cannot_req), Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                IngredientRequestView.this.runOnUiThread(() -> {
                    if (response.code() == Utilities.HTTP_511)
                    {

                        CharSequence s = getString(R.string.msg_token_expired);

                        Toast t = Toast.makeText(IngredientRequestView.this, s, Toast.LENGTH_SHORT);
                        t.show();

                    }
                    else if(response.isSuccessful()) {
                        Log.d("test", "call ok");
                        Toast toast = Toast.makeText(IngredientRequestView.this, getString(R.string.rq_msg_req_successful), Toast.LENGTH_LONG);
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
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));
        String email = sharedPref.getString(getString(R.string.prf_eml), getString(R.string.prf_eml_def));

        //get requests from server
        Request req = new Request.Builder()
                .url(getString(R.string.http_inred_req_url))
                .addHeader(getString(R.string.http_args_email), email)
                .addHeader(getString(R.string.http_args_userToken), tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == Utilities.HTTP_511)
                {

                    CharSequence s = getString(R.string.prf_token);

                    Toast t = Toast.makeText(IngredientRequestView.this, s, Toast.LENGTH_SHORT);
                    t.show();
                }
                else if(response.isSuccessful())
                {
                    assert response.body() != null;
                    String res = response.body().string();

                    IngredientRequestView.this.runOnUiThread(() -> {

                        //TODO add expiry logic
                        IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                        ingredientRequests = new ArrayList<>();

                        ingredientRequests.addAll(Arrays.asList(userArray));

                        recyclerView = findViewById(R.id.rq_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(IngredientRequestView.this));
                        recyclerView.setAdapter(new IngredientRequestAdaptor(IngredientRequestView.this, IngredientRequestView.this, ingredientRequests));

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