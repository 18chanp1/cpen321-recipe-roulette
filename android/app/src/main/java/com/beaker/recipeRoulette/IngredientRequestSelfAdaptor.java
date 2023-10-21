package com.beaker.recipeRoulette;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IngredientRequestSelfAdaptor
extends IngredientRequestAdaptor{

    public IngredientRequestSelfAdaptor(Context context, List<IngredientRequest> items) {
        super(context, items);
    }

    @Override
    public void onBindViewHolder (@NonNull IngredientRequestHolder holder, int position) {
        IngredientRequest item = items.get(position);

        holder.nameView.setText(item.ingredientName);
        holder.emailView.setText(item.getRequestor());

        String url = item.getImage();
        Picasso.with(this.context).load(url).into(holder.imageView);

        //buttons
        holder.donateIngredientView.setText("Cancel");
        holder.donateIngredientView.setOnClickListener(view ->
        {
            Context c = IngredientRequestSelfAdaptor.this.context;
            //send an http request.

            //get token
            SharedPreferences sharedPref =
                    this.context.getSharedPreferences(this.context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
            String tok = sharedPref.getString("TOKEN", "NOTOKEN");

            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();
            String json = gson.toJson(new IngredientRequestAcceptTicket(tok, item.reqID));

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json, JSON);
            String acceptUrl = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests/self/delete";

            Request req = new Request.Builder()
                    .url(acceptUrl)
                    .addHeader("userToken", tok)
                    .addHeader("reqID", item.reqID)
                    .addHeader("loc", "donate ingredients")
                    .post(body)
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    ContextCompat.getMainExecutor(IngredientRequestSelfAdaptor.this.context).execute(() ->
                    {
                        Toast toast = Toast.makeText(context, "Unable to donate, try again later.", Toast.LENGTH_LONG);
                        toast.show();
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful())
                    {
                        ContextCompat.getMainExecutor(IngredientRequestSelfAdaptor.this.context).execute(() ->
                        {

                            Toast toast = Toast.makeText(context, response.message(), Toast.LENGTH_LONG);
                            toast.show();
                        });
                    }
                }
            });
        });

    }
}
