package com.s23010255.waste_watcher;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;

public class GetpointaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imagePreview;
    private TextView locationTextView;
    private EditText descriptionEditText;
    private Uri imageUri;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap gMap;
    private LatLng currentLatLng;
    private Button submitButton, uploadAfterImageButton;

    private ActivityResultLauncher<Intent> pickAfterImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpointa);

        imagePreview = findViewById(R.id.imagePreviewafter);
        locationTextView = findViewById(R.id.textView19);
        descriptionEditText = findViewById(R.id.editTextTextMultiLine);
        submitButton = findViewById(R.id.button6);
        uploadAfterImageButton = findViewById(R.id.buttonUploadAfter);

        // Image picker launcher
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
            if (imageUri != null)
                submitIntent.putExtra("after_image_uri", imageUri.toString());
            startActivity(submitIntent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            gMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    gMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));

                    String locationText = String.format("Lat: %.5f, Lng: %.5f",
                            location.getLatitude(), location.getLongitude());
                    locationTextView.setText(locationText);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 &&
                grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }
}
