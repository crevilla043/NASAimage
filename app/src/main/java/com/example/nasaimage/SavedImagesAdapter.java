package com.example.nasaimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class SavedImagesAdapter extends RecyclerView.Adapter<SavedImagesAdapter.ViewHolder> {
    private Context context;
    private List<SavedImage> savedImages;

    public SavedImagesAdapter(Context context, List<SavedImage> savedImages) {
        this.context = context;
        this.savedImages = savedImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedImage savedImage = savedImages.get(position);
        holder.tvDate.setText("Date: " + savedImage.getDate());

        File imgFile = new File(context.getFilesDir(), savedImage.getFileName());
        if (imgFile.exists()) {
            holder.imageView.setImageURI(Uri.fromFile(imgFile));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewImageActivity.class);
            intent.putExtra("imageUri", Uri.fromFile(imgFile).toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return savedImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

