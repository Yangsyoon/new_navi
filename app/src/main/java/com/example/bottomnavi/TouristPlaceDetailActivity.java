package com.example.bottomnavi;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TouristPlaceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_place_detail);

        TouristPlace place = TouristPlaceDataHolder.getInstance().getPlace();

        if (place != null) {
            TextView placeNameTextView = findViewById(R.id.placeNameTextView);
            TextView locationTextView = findViewById(R.id.locationTextView);
            TextView addressTextView = findViewById(R.id.addressTextView);
            TextView descriptionTextView = findViewById(R.id.descriptionTextView);
            TextView phoneTextView = findViewById(R.id.phoneTextView);
            ImageView imageView = findViewById(R.id.imageView);

            placeNameTextView.setText(place.getPlaceName());
            locationTextView.setText("Latitude: " + place.getLatitude() + ", Longitude: " + place.getLongitude());
            addressTextView.setText("Address: " + place.getAddress());
            descriptionTextView.setText("Description: " + place.getDescription());
            phoneTextView.setText("Phone: " + place.getPhone());

            String photoUrl = place.getPhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Log.d(TAG, "Decoding Base64 photo URL: " + photoUrl);
                Bitmap bitmap = base64ToBitmap(photoUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.d(TAG, "Decoded bitmap is null");
                }
            } else {
                Log.d(TAG, "Photo URL is null or empty");
            }
        } else {
            Log.e(TAG, "TouristPlace data is null");
        }
    }

    private Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            Log.e(TAG, "Error decoding Base64 string", e);
            return null;
        }
    }
}
