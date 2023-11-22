package com.beaker.reciperoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlavorProfile extends AppCompatActivity {
    private TextView flavorProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flavor_profile);

        flavorProfile = findViewById(R.id.fp_result);

        //make an http request for the flavor profile
        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/flavourprofile")
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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 511)
                {

                    CharSequence s = "Exit the app and try again";

                    Toast t = Toast.makeText(FlavorProfile.this, s, Toast.LENGTH_SHORT);
                    t.show();
                }
                else if(response.isSuccessful())
                {
                    runOnUiThread(() -> {
                        try {
                            String result = Objects.requireNonNull(response.body()).string();
                            flavorProfile.setText(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                }
            }
        });
    }
}