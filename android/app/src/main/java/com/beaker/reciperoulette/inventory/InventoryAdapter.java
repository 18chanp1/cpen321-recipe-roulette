package com.beaker.reciperoulette.inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;
import com.beaker.Utilities;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private static final int TIME_WINDOW = 60000;

    private static final String TAG = "InventoryAdapter";

    public InventoryAdapter(Context context, InventoryView inventoryView, List<IngredientV2> items) {
        this.context = context;
        this.inventoryView = inventoryView;
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

        if(item == null) throw new IllegalArgumentException();

        TextView title = holder.getItemTitleView();
        TextView expiry = holder.getExpiryDateView();
        ImageView image = holder.getImageView();
        TextView newItem = holder.getNewItemView();

        if(title == null || expiry == null ||
                image == null || newItem == null)
            throw new IllegalArgumentException();

        if(item.name == null) title.setText("");
        else  title.setText(item.name);

        if(item.date == null || item.date.length < 1) expiry.setText("");
        else
        {
            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.sdf_server), Locale.getDefault());
            SimpleDateFormat sdfReadable = new SimpleDateFormat(context.getString(R.string.sdf_readable), Locale.getDefault());
            try {
                // Calendar c = Calendar.getInstance();
                Date d = sdf.parse(item.date[0]);
                String result = sdfReadable.format(d);

                expiry.setText(context.getString(R.string.inv_expires) + result);

            } catch (ParseException e) {
               Log.d("INVENTORYADAPTER", "Failed to parse date");
                expiry.setText(item.date[0]);
            }
        }

        //TODO fix expiry, waiting for Josh.
        image.setImageResource(R.drawable.carrot);
        newItem.setVisibility(View.INVISIBLE);

        InventoryDateHelper(item, holder);
    }

    private void InventoryDateHelper(IngredientV2 item, IngredientHolder holder) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.sdf_server), Locale.getDefault());

        try {
            Date d1 = sdf.parse(item.date[item.date.length - 1]);
            Date d2 = Calendar.getInstance().getTime();

            assert d1 != null;
            long diff = d2.getTime() - (d1.getTime() - Utilities.SEVEN_DAYS);

            // 2 minutes
            // TODO move to some var later, and deal with timezones
            if(diff < TIME_WINDOW)
            {
                holder.getNewItemView().setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        String tok = sharedPref.getString(context.getString(R.string.prf_token), context.getString(R.string.prf_token_def));
        String email = sharedPref.getString(context.getString(R.string.prf_eml), context.getString(R.string.prf_eml_def));

        Gson gson = new Gson();
        String json = gson.toJson(new DeleteTicket(email, new String[]{item.name}));

        MediaType JSON = MediaType.get(context.getString(R.string.http_json_type));
        RequestBody body = RequestBody.create(json, JSON);

        //get requests from server
        Request req = new Request.Builder()
                .url(context.getString(R.string.http_inv_update_url))
                .addHeader(context.getString(R.string.http_args_userid), email)
                .addHeader(context.getString(R.string.http_args_userToken), tok)
                .put(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d(TAG, String.valueOf(response.code()));
                if (response.code() == Utilities.HTTP_511)
                {
                    CharSequence s = context.getString(R.string.msg_token_expired);
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
