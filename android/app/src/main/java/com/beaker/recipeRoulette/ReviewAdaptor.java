package com.beaker.recipeRoulette;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void renderImage()
    {

    }

}
