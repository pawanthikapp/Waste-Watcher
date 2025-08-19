package com.s23010255.waste_watcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetpointaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imagePreview;
    private TextView locationTextView;
    private EditText descriptionEditText, addressInput;
    private Uri imageUri;
    private GoogleMap gMap;
    private Button submitButton, uploadAfterImageButton, searchBtn;

    private ActivityResultLauncher<Intent> pickAfterImageLauncher;


    private int totalPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpointa);

        imagePreview = findViewById(R.id.imagePreviewafter);
        locationTextView = findViewById(R.id.textView19);
        descriptionEditText = findViewById(R.id.editTextTextMultiLine);

        addressInput = findViewById(R.id.textloc);

        submitButton = findViewById(R.id.button6);
        uploadAfterImageButton = findViewById(R.id.buttonUploadAfter);
        searchBtn = findViewById(R.id.searchbtn);


        totalPoints = getIntent().getIntExtra("total_points", 0);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        pickAfterImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            imageUri = uri;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                imagePreview.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        uploadAfterImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickAfterImageLauncher.launch(intent);
        });

        submitButton.setOnClickListener(view -> {
            String desc = descriptionEditText.getText().toString();
            String locationText = locationTextView.getText().toString();

            Intent submitIntent = new Intent(GetpointaActivity.this, SubmitSuccessActivity.class);
            submitIntent.putExtra("description", desc);
            submitIntent.putExtra("location", locationText);
            submitIntent.putExtra("total_points", totalPoints);
            if (imageUri != null) {
                submitIntent.putExtra("after_image_uri", imageUri.toString());
            }
            startActivity(submitIntent);
        });

        searchBtn.setOnClickListener(v -> {
            String location = addressInput.getText().toString().trim();
            if (!location.isEmpty()) {
                findLocationOnMap(location);
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    private void findLocationOnMap(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                gMap.clear();
                gMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // Update location TextView with coordinates
                locationTextView.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);

            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show();
        }
    }
}
