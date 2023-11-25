package com.beaker.reciperoulette.RecipeFacebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beaker.reciperoulette.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReviewDetailed extends AppCompatActivity {
    /**
     *     private TextView titleText;
     *     private TextView reviewText;
     *     private ImageView image;
     */

    private TextView ratingText;
    private TextView authorText;
    private Button like_but;
    private Review review;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detailed);

        TextView titleText = findViewById(R.id.detailed_title);
        TextView reviewText = findViewById(R.id.detailed_text);
        ImageView image = findViewById(R.id.detailed_image);

        authorText = findViewById(R.id.detailed_author);
        like_but = findViewById(R.id.like_button);
        ratingText = findViewById(R.id.detailed_rating);

        Intent in = getIntent();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Review r = in.getParcelableExtra("REVIEW", Review.class);
            titleText.setText(r.getTitle());
            authorText.setText(r.getAuthor());
            reviewText.setText(Html.fromHtml(r.getReview(), Html.FROM_HTML_MODE_COMPACT));
            ratingText.setText(r.getRating());
            Picasso.with(this).load(r.getImage()).into(image);

            review = r;
        } else {
            Review r = in.getParcelableExtra("REVIEW");

            titleText.setText(r.getTitle());
            authorText.setText(r.getAuthor());
            reviewText.setText(Html.fromHtml(r.getReview(), Html.FROM_HTML_MODE_COMPACT));
            ratingText.setText("Rating: " + String.valueOf(r.getRating()));
            Picasso.with(this).load(r.getImage()).into(image);

            review = r;

            like_but.setOnClickListener(view -> {
                likeListener(true, r.getId());
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
        String json = gson.toJson(new LikeTicket(tok, id, like, authorText.getText().toString()));

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews/like")
                .addHeader("userToken", tok)
                .addHeader("id", String.valueOf(id))
                .addHeader("email", authorText.getText().toString())
                .addHeader("like", String.valueOf(like))
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
                        review.rating++;
                        runOnUiThread(() -> ratingText.setText("Rating: " + String.valueOf(review.rating)));


                    }

                    else {
                        like_but.setBackgroundColor(Color.rgb(92,70,154));
                    }

                }
            }
        });

    }
}