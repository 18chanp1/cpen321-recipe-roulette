package com.beaker.reciperoulette.reviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.beaker.Utilities;
import com.beaker.reciperoulette.R;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteReview extends AppCompatActivity {
    private TextView titleText;
    private TextView reviewText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_write_review);

        titleText = findViewById(R.id.rev_titleEntry);
        reviewText = findViewById(R.id.rev_writereview_body);
        Button submitButton = findViewById(R.id.rev_review_submit);

        assert titleText != null;
        assert reviewText != null;
        assert submitButton != null;

        submitButton.setOnClickListener(view -> setupButtonHandler());
    }

    private void setupButtonHandler()
    {
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));
        String email = sharedPref.getString(getString(R.string.prf_eml), getString(R.string.prf_eml_def));

        if (titleText.getText().toString().length() == 0 || reviewText.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(WriteReview.this,
                    getText(R.string.rev_cust_empty), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(new CustomReview(email, titleText.getText().toString(), reviewText.getText().toString()));


        MediaType JSON = MediaType.get(getString(R.string.http_json_type));
        RequestBody body = RequestBody.create(json, JSON);

        Request postNewReq = new Request.Builder()
                .url(getString(R.string.http_custom_rev_url))
                .addHeader(getString(R.string.http_args_userToken), tok)
                .addHeader(getString(R.string.http_args_email), email)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(postNewReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                WriteReview.this.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(WriteReview.this, getString(R.string.revc_msg_submit_fail), Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                WriteReview.this.runOnUiThread(() -> {
                    if (response.code() == Utilities.HTTP_511) {

                        CharSequence s = getString(R.string.msg_token_expired);

                        Toast t = Toast.makeText(WriteReview.this, s, Toast.LENGTH_SHORT);
                        t.show();

                    } else if (response.isSuccessful()) {
                        Log.d("test", "call ok");
                        Toast toast = Toast.makeText(WriteReview.this, getString(R.string.rq_msg_req_successful), Toast.LENGTH_LONG);
                        toast.show();

                        runOnUiThread(() -> finish());
                    }
                });
            }
        });
    }
}