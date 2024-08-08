package com.example.nasaimage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView = findViewById(R.id.imageViewFull);

        // Get the image URI from the intent
        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("imageUri");

        // Load the image using Picasso
        if (imageUri != null) {
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}