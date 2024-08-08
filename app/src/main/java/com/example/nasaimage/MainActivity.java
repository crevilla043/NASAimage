package com.example.nasaimage;

import static com.example.nasaimage.R.*;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.type.Date;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.widget.TextView;
import android.widget.ImageView;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedDate;
    private TextView tvImageUrl;
    private ImageView imageView;
    private Button btnSaveImage;
    private String imageUrl;
    private String hdImageUrl;
    private String selectedDate;
    private List<SavedImage> savedImages;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvImageUrl = findViewById(R.id.tvImageUrl);
        Button btnSelectDate = findViewById(R.id.btnSelectDate);
        Button btnFetchImage = findViewById(R.id.btnFetchImage);
        imageView = findViewById(R.id.imageView);
        btnSaveImage = findViewById(R.id.btnSaveImage);
        Button btnViewSavedImages = findViewById(R.id.btnViewSavedImages);

        sharedPreferences = getSharedPreferences("SavedImages", MODE_PRIVATE);
        gson = new Gson();
        loadSavedImages();

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        btnFetchImage.setOnClickListener(v -> fetchImage());

        btnSaveImage.setOnClickListener(v -> saveImage());

        btnViewSavedImages.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavedImagesActivity.class);
            startActivity(intent);
        });

        // Set up a click listener for the image URL to open it in the browser
        tvImageUrl.setOnClickListener(v -> {
            if (hdImageUrl != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hdImageUrl));
                startActivity(browserIntent);
            }
        });
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(calendar.getTime());
        tvSelectedDate.setText("Selected Date: " + selectedDate);
    }

    private void fetchImage() {
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }

        String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=" + selectedDate;

        new FetchImageTask().execute(apiUrl);
    }

    private void saveImage() {
        if (imageUrl == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        new SaveImageTask().execute(imageUrl);
    }

    private void loadSavedImages() {
        String json = sharedPreferences.getString("images", null);
        Type type = new TypeToken<ArrayList<SavedImage>>() {}.getType();
        savedImages = gson.fromJson(json, type);

        if (savedImages == null) {
            savedImages = new ArrayList<>();
        }
    }

    private void saveImageData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(savedImages);
        editor.putString("images", json);
        editor.apply();
    }

    private class FetchImageTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject jsonObject = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    StringBuilder result = new StringBuilder();
                    int byteRead;
                    while ((byteRead = in.read()) != -1) {
                        result.append((char) byteRead);
                    }
                    jsonObject = new JSONObject(result.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("FetchImageTask", "Error fetching image URL", e);
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    imageUrl = jsonObject.getString("url");
                    hdImageUrl = jsonObject.getString("hdurl");
                    String date = jsonObject.getString("date");

                    tvSelectedDate.setText("Date: " + date);
                    tvImageUrl.setText("HD Image URL: " + hdImageUrl);
                    imageView.setVisibility(View.VISIBLE);
                    btnSaveImage.setVisibility(View.VISIBLE);

                    Picasso.get().load(imageUrl).into(imageView);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to parse image data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                String fileName = "NASA_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime()) + ".png";
                File file = new File(getFilesDir(), fileName);
                FileOutputStream output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.close();

                savedImages.add(new SavedImage(selectedDate, imageUrl, hdImageUrl, fileName));
                saveImageData();

                return true;
            } catch (Exception e) {
                Log.e("SaveImageTask", "Error saving image", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}