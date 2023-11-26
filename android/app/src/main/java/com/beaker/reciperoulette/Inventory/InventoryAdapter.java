package com.beaker.reciperoulette.Inventory;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.Inventory.IngredientHolder;
import com.beaker.reciperoulette.Inventory.IngredientV2;
import com.beaker.reciperoulette.R;
import com.beaker.reciperoulette.RecipeFacebook.LikeTicket;
import com.beaker.reciperoulette.RecipeFacebook.ReviewDetailed;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InventoryAdapter extends RecyclerView.Adapter<IngredientHolder> {

    Context context;
    InventoryView inventoryView;
    List<IngredientV2> items;

    private static final String TAG = "InventoryAdapter";

    public InventoryAdapter(Context context, InventoryView inventoryView, List<IngredientV2> items) {
        this.context = context;
        this.inventoryView = inventoryView;
        this.items = items;
    }

    public InventoryAdapter(Context context, List<IngredientV2> items)
    {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientHolder(LayoutInflater.from(context).inflate(R.layout.inv_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        IngredientV2 item = items.get(position);
        holder.getItemTitleView().setText(item.name);
        //TODO fix this.
        holder.getExpiryDateView().setText(item.date[0]);
        Log.d("InventoryAdapter", "setting item names");

        holder.getImageView().setImageResource(R.drawable.carrot);

//        holder.getConsumeButtonView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        //handle the buttons
        holder.getConsumeButtonView().setOnClickListener(view -> {
            Log.d("InventoryAdapter", "Button pressed");
            consumeItem(item);

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void consumeItem(IngredientV2 item)
    {
        //make a request to the server to delete
        //get tokens
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        Gson gson = new Gson();
        String json = gson.toJson(new DeleteTicket(email, new String[]{item.name}));

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/foodInventoryManager/update")
                .addHeader("userToken", tok)
                .addHeader("email", email)
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        Log.d("InventoryAdapter", json);
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, String.valueOf(response.code()));


                if (response.code() == 511)
                {

                    CharSequence s = "Exit the app and try again";

                    Toast t = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                    t.show();

                }
                else if(response.isSuccessful())
                {
                    //tell daddy to reload
                    inventoryView.loadInventory();
                }
            }
        });


    }


}
