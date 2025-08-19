package com.s23010255.waste_watcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LatLng> binList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String BIN_LIST_KEY = "bin_list";
    private EditText searchInput;


    private Map<String, LatLng> predefinedBins = new HashMap<String, LatLng>() {{
        put("Kurunegala", new LatLng(7.4867, 80.3649));
        put("Kandy", new LatLng(7.2906, 80.6337));
        put("Nawala", new LatLng(6.9100, 79.9400));
        put("Matara", new LatLng(5.9481, 80.5357));
        put("Jaffna", new LatLng(9.6615, 80.0255));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        sharedPreferences = getSharedPreferences("MapPrefs", Context.MODE_PRIVATE);
        loadBins();

        searchInput = findViewById(R.id.searchInput);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            String locationName = searchInput.getText().toString();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            }
            return true;
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        for (Map.Entry<String, LatLng> entry : predefinedBins.entrySet()) {
            addBin(entry.getValue(), entry.getKey());
        }


        for (LatLng bin : binList) {
            mMap.addMarker(new MarkerOptions()
                    .position(bin)
                    .title("Saved Bin")
                    .icon(BitmapDescriptorFactory.fromBitmap(getResizedMarker(R.drawable.nearbin, 80, 80))));
        }


        mMap.setOnMapClickListener(latLng -> {
            findNearestBin(latLng);

            new AlertDialog.Builder(this)
                    .setTitle("Add Bin")
                    .setMessage("Do you want to add a bin at this location?")
                    .setPositiveButton("Yes", (dialog, which) -> addBin(latLng, "User Bin"))
                    .setNegativeButton("No", null)
                    .show();
        });
    }


    private void addBin(LatLng location, String title) {
        if (!binList.contains(location)) {
            binList.add(location);
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(getResizedMarker(R.drawable.nearbin, 80, 80))));
            saveBins();
        }
    }


    private Bitmap getResizedMarker(int resId, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), resId);
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    private void saveBins() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(binList);
        editor.putString(BIN_LIST_KEY, json);
        editor.apply();
    }

    private void loadBins() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(BIN_LIST_KEY, null);
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        List<LatLng> savedBins = gson.fromJson(json, type);
        if (savedBins != null) {
            binList = savedBins;
        }
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(locationName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


                findNearestBin(latLng);


                new AlertDialog.Builder(this)
                        .setTitle("Add Bin")
                        .setMessage("Do you want to add a bin at this location?")
                        .setPositiveButton("Yes", (dialog, which) -> addBin(latLng, "User Bin"))
                        .setNegativeButton("No", null)
                        .show();

            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder error", Toast.LENGTH_SHORT).show();
        }
    }


    private void findNearestBin(LatLng current) {
        if (binList.isEmpty()) return;

        LatLng nearest = null;
        float minDistance = Float.MAX_VALUE;
        float[] result = new float[1];

        for (LatLng bin : binList) {
            Location.distanceBetween(current.latitude, current.longitude, bin.latitude, bin.longitude, result);
            if (result[0] < minDistance) {
                minDistance = result[0];
                nearest = bin;
            }
        }

        if (nearest != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(nearest)
                    .title("Nearest Bin")
                    .icon(BitmapDescriptorFactory.fromBitmap(getResizedMarker(R.drawable.nearbin, 80, 80))));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearest, 15));
            Toast.makeText(this, "Nearest bin found", Toast.LENGTH_SHORT).show();
        }
    }
}
