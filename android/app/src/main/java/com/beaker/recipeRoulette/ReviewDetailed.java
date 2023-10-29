package com.beaker.recipeRoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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

public class ReviewDetailed extends AppCompatActivity {
    private TextView titleText;
    private TextView authorText;
    private TextView reviewText;
    private TextView ratingText;
    private ImageView image;
    private Button like_but;
    private Button dislike_but;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detailed);

        titleText = findViewById(R.id.detailed_title);
        authorText = findViewById(R.id.detailed_author);
        reviewText = findViewById(R.id.detailed_text);
        ratingText = findViewById(R.id.detailed_rating);
        image = findViewById(R.id.detailed_image);
        like_but = findViewById(R.id.like_button);
        dislike_but = findViewById(R.id.dislike_but);

        Intent in = getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            titleText.setText(in.getParcelableExtra("REVIEW", Review.class).getTitle());
            authorText.setText(in.getParcelableExtra("REVIEW", Review.class).getAuthor());
            reviewText.setText(in.getParcelableExtra("REVIEW", Review.class).getReview());
            ratingText.setText(in.getParcelableExtra("REVIEW", Review.class).getRating());
            Picasso.with(this).load(in.getParcelableExtra("REVIEW", Review.class).getImage()).into(image);
        } else {
            Review r = in.getParcelableExtra("REVIEW");

            titleText.setText(r.getTitle());
            authorText.setText(r.getAuthor());
            reviewText.setText(r.getReview());
            ratingText.setText("Rating: " + String.valueOf(r.getRating()));
            Picasso.with(this).load(r.getImage()).into(image);

            like_but.setOnClickListener(view -> {
                likeListener(true, r.getID());
            });

            dislike_but.setOnClickListener(view ->
            {
                likeListener(false, r.getID());
            });
        }








    }

    private void likeListener(boolean like, int id)
    {
        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");

        Gson gson = new Gson();
        String json = gson.toJson(new LikeTicket(tok, id, like));

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews/like")
                .addHeader("userToken", tok)
                .addHeader("id", String.valueOf(id))
                .post(body)
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

                    Toast t = Toast.makeText(ReviewDetailed.this, s, Toast.LENGTH_SHORT);
                    t.show();

                }
                else if(response.isSuccessful())
                {
                    if(like) {
                        like_but.setBackgroundColor(Color.GREEN);
                        dislike_but.setBackgroundColor(Color.rgb(92,70,154));
                    }

                    else {
                        like_but.setBackgroundColor(Color.rgb(92,70,154));
                        dislike_but.setBackgroundColor(Color.GREEN);
                    }

                }
            }
        });

    }
}