package com.example.nasaimage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// SavedImagesActivity.java
public class SavedImagesActivity extends AppCompatActivity {

    private List<SavedImage> savedImages;
    private ListView listView;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);

        gson = new Gson();
        listView = findViewById(R.id.listView);
        loadSavedImages();

        SavedImagesAdapter adapter = new SavedImagesAdapter(this, savedImages);
        listView.setAdapter(adapter);
    }

    private void loadSavedImages() {
        String json = getSharedPreferences("SavedImages", MODE_PRIVATE).getString("images", null);
        Type type = new TypeToken<ArrayList<SavedImage>>() {}.getType();
        savedImages = gson.fromJson(json, type);

        if (savedImages == null) {
            savedImages = new ArrayList<>();
        }
    }

    private void saveImages() {
        String json = gson.toJson(savedImages);
        getSharedPreferences("SavedImages", MODE_PRIVATE).edit().putString("images", json).apply();
    }

    private class SavedImagesAdapter extends ArrayAdapter<SavedImage> {

        private final Context context;
        private final List<SavedImage> images;

        public SavedImagesAdapter(Context context, List<SavedImage> images) {
            super(context, R.layout.item_saved_image, images);
            this.context = context;
            this.images = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.item_saved_image, parent, false);
            }

            SavedImage currentImage = images.get(position);

            TextView tvDate = listItemView.findViewById(R.id.tvDate);
            tvDate.setText("Date: " + currentImage.getDate());

            TextView tvUrl = listItemView.findViewById(R.id.tvUrl);
            tvUrl.setText("URL: " + currentImage.getUrl());

            ImageView imageViewThumbnail = listItemView.findViewById(R.id.imageViewThumbnail);
            File imgFile = new File(context.getFilesDir(), currentImage.getFileName());
            if (imgFile.exists()) {
                imageViewThumbnail.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }

            ImageButton btnDelete = listItemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                deleteImage(currentImage);
                images.remove(position);
                notifyDataSetChanged();
            });

            return listItemView;
        }

        private void deleteImage(SavedImage image) {
            // Delete the image file from the device storage
            File imgFile = new File(context.getFilesDir(), image.getFileName());
            if (imgFile.exists()) {
                imgFile.delete();
            }
            // Remove the image from the list and save changes
            savedImages.remove(image);
            saveImages();
        }
    }
}