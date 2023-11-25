package com.beaker.reciperoulette.RecipeFacebook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdaptor extends RecyclerView.Adapter<ReviewHolder> {

    Context context;
    List<Review> items;

    public ReviewAdaptor(Context context, List<Review> items)
    {
        this.context = context;
        this.items = items;
    }
    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(context).inflate(R.layout.review_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.nameView.setText(items.get(position).getTitle());
        holder.emailView.setText(items.get(position).getAuthor());
        Log.d("ReviewAdaptor", "setting item names");

        String url = items.get(position).getImage();
        Picasso.with(this.context).load(url).into(holder.imageView);

        //handle the buttons
        holder.readMoreView.setOnClickListener(view -> {
            Context c = ReviewAdaptor.this.context;
            Intent readMoreIntent = new Intent(c, ReviewDetailed.class);
            readMoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            readMoreIntent.putExtra("REVIEW", items.get(holder.getAbsoluteAdapterPosition()));
            c.startActivity(readMoreIntent);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
