package com.beaker.reciperoulette.reviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.beaker.Utilities;
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

        assert titleText != null;
        assert reviewText != null;
        assert image != null;
        assert authorText != null;
        assert like_but != null;
        assert ratingText != null;

        Intent in = getIntent();


        Review r = in.getParcelableExtra("REVIEW");

        assert r != null;
        if (r.getTitle() == null || r.getTitle().length() == 0)
        {
            titleText.setText(getString(R.string.rev_msg_no_title));
        } else
        {
            titleText.setText(r.getTitle());
        }

        if(r.getAuthor() == null)
        {
            authorText.setText("");
        } else
        {
            authorText.setText(r.getAuthor());
        }

        if(r.getReview() == null)
        {
            reviewText.setText("");
        }
        else
        {
            reviewText.setText(Html.fromHtml(r.getReview(), Html.FROM_HTML_MODE_COMPACT));
        }

        String newRatingText = getString(R.string.rev_rating_pref) + r.getRating();
        ratingText.setText(newRatingText);

        if(r.getImage() != null && r.getImage().length() != 0)
        {
            Picasso.with(this).load(r.getImage()).into(image);
        }
        review = r;

        like_but.setOnClickListener(view -> likeListener(r.getId()));

    }

    private void likeListener(String id)
    {
        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));

        if(tok.equals(getString(R.string.prf_token_def)))
            throw new IllegalStateException();

        Gson gson = new Gson();
        String json = gson.toJson(new LikeTicket(tok, id, true, authorText.getText().toString()));

        MediaType JSON = MediaType.get(getString(R.string.http_json_type));
        RequestBody body = RequestBody.create(json, JSON);

        //get requests from server
        Request req = new Request.Builder()
                .url(getString(R.string.http_rev_like_url))
                .addHeader(getString(R.string.http_args_userToken), tok)
                .addHeader(getString(R.string.http_args_id), String.valueOf(id))
                .addHeader(getString(R.string.http_args_email), authorText.getText().toString())
                .addHeader(getString(R.string.http_args_like), String.valueOf(true))
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == Utilities.HTTP_511)
                {

                    CharSequence s = getString(R.string.msg_token_expired);

                    Toast t = Toast.makeText(ReviewDetailed.this, s, Toast.LENGTH_SHORT);
                    t.show();

                }
                else if(response.isSuccessful())
                {
                    like_but.setBackgroundColor(Color.GREEN);
                    review.rating++;
                    String newRatingText = getString(R.string.rev_rating_pref) + review.rating;
                    runOnUiThread(() -> ratingText.setText(newRatingText));

                }
            }
        });

    }
}