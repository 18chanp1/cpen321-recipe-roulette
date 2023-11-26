package com.beaker.reciperoulette.IngredientRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

public class IngredientRequestAdaptor extends RecyclerView.Adapter<IngredientRequestHolder> {
    Context context;
    IngredientRequestView ingredientRequestView;
    List<? extends IngredientRequest> items;

    public IngredientRequestAdaptor(Context context, List<? extends IngredientRequest> items) {
        this.context = context;
        this.items = items;

        if(this.context == null || this.items == null) throw new IllegalArgumentException();
    }

    public IngredientRequestAdaptor(Context context, IngredientRequestView ingredientRequestView, List<? extends IngredientRequest> items) {
        this.context = context;
        this.ingredientRequestView = ingredientRequestView;
        this.items = items;

        if(this.context == null || this.items == null || this.ingredientRequestView == null)
            throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public IngredientRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientRequestHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_req_item, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull IngredientRequestHolder holder, int position) {
        IngredientRequest item = items.get(position);

        if(item.ingredientName == null) item.ingredientName = "";
        if(item.userId == null) item.userId = "";

        holder.nameView.setText(item.ingredientName);
        holder.emailView.setText(item.userId);

        if(item.getImage() != null)
        {
            String url = item.getImage();
            Picasso.with(this.context).load(url).into(holder.imageView);
        }

        //buttons
        holder.donateIngredientView.setOnClickListener(view ->
        {
            //send an http request.
            //get token
            SharedPreferences sharedPref =
                    this.context.getSharedPreferences(this.context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
            String tok = sharedPref.getString(context.getString(R.string.prf_token), context.getString(R.string.prf_token_def));
            String email = sharedPref.getString(context.getString(R.string.prf_eml), context.getString(R.string.prf_eml_def));

            if(tok.equals(context.getString(R.string.prf_token_def)) ||
            email.equals(context.getString(R.string.prf_eml_def)))
            {
                throw new IllegalStateException();
            }

            OkHttpClient client = new OkHttpClient();

            Gson gson = new Gson();
            String json = gson.toJson(new IngredientRequestAcceptTicket(tok, item.reqID, email));

            MediaType JSON = MediaType.get(context.getString(R.string.http_json_type));
            RequestBody body = RequestBody.create(json, JSON);
            String acceptUrl = context.getString(R.string.http_inred_req_url);

            Request req = new Request.Builder()
                    .url(acceptUrl)
                    .addHeader(context.getString(R.string.http_args_userToken), tok)
                    .addHeader(context.getString(R.string.http_args_userToken), item.reqID)
                    .addHeader(context.getString(R.string.http_args_email), email)
                    .post(body)
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    ContextCompat.getMainExecutor(IngredientRequestAdaptor.this.context).execute(() ->
                    {
                        Toast toast = Toast.makeText(context, context.getString(R.string.cannot_donate), Toast.LENGTH_LONG);
                        toast.show();
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.code() == 511)
                    {
                        CharSequence s = context.getString(R.string.msg_token_expired);

                        ContextCompat.getMainExecutor(context).execute(()  -> {
                            Toast t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                            t.show();
                        });

                    }
                    else if (response.isSuccessful())
                    {
                        //Toast the response
                        ContextCompat.getMainExecutor(IngredientRequestAdaptor.this.context).execute(() ->
                        {
                            Toast toast = Toast.makeText(context, response.message(), Toast.LENGTH_LONG);
                            toast.show();
                        });
                        //TODO need to add display for trade details.

                        //reload the donations on the view
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = () -> ingredientRequestView.loadDonations();
                        mainHandler.post(myRunnable);

                    }
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
