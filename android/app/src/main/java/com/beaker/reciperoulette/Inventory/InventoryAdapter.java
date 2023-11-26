package com.beaker.reciperoulette.Inventory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.Inventory.IngredientHolder;
import com.beaker.reciperoulette.Inventory.IngredientV2;
import com.beaker.reciperoulette.R;

import java.util.Arrays;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<IngredientHolder> {

    Context context;
    List<IngredientV2> items;

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
        holder.getItemTitleView().setText(items.get(position).name);
        //TODO fix this.
        holder.getExpiryDateView().setText(items.get(position).date[0]);
        Log.d("InventoryAdapter", "setting item names");

        holder.getImageView().setImageResource(R.drawable.carrot);

        //handle the buttons
        holder.getConsumeButtonView().setOnClickListener(view -> {
            Context c = InventoryAdapter.this.context;
            //do stuff
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
