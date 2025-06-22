package com.s23010255.waste_watcher;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

    public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private FusedLocationProviderClient fusedLocationProviderClient;
        private LatLng userLocation;
        private LatLng selectedLocation;
        private Polyline currentRoute;
        private final List<LatLng> binLocations = new ArrayList<>();

        EditText searchInput;
        Button findNearestBtn, addBinHereBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);

            searchInput = findViewById(R.id.searchInput);
            findNearestBtn = findViewById(R.id.findNearestBtn);
            addBinHereBtn = findViewById(R.id.addBinHereBtn);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);

            // Search input handler
            searchInput.setOnEditorActionListener((v, actionId, event) -> {
                String query = searchInput.getText().toString();
                if (!query.isEmpty()) {
                    geocodeAndPlace(query);
                }
                return true;
            });

            // Add bin button
            addBinHereBtn.setOnClickListener(v -> {
                if (selectedLocation != null) {
                    binLocations.add(selectedLocation);
                    mMap.addMarker(new MarkerOptions()
                            .position(selectedLocation)
                            .title("User Bin")
                            .snippet("Added by user")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.nearbin)));
                }
            });

            // Find nearest bin from selected location
            findNearestBtn.setOnClickListener(v -> {
                if (selectedLocation != null && !binLocations.isEmpty()) {
                    LatLng nearest = findNearestBin(selectedLocation);
                    fetchRouteFromGoogle(selectedLocation, nearest);
                }
            });
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }

            mMap.setMyLocationEnabled(true);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    selectedLocation = userLocation;

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));

                    // Predefined bins
                    binLocations.add(new LatLng(6.9271, 79.8612));
                    binLocations.add(new LatLng(6.9330, 79.8500));
                    binLocations.add(new LatLng(6.9100, 79.8600));

                    for (LatLng bin : binLocations) {
                        mMap.addMarker(new MarkerOptions()
                                .position(bin)
                                .title("Public Bin")
                                .snippet("Recyclable")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.nearbin)));
                    }
                }
            });

            // Tap map to select new location
            mMap.setOnMapLongClickListener(latLng -> {
                selectedLocation = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Point"));
            });
        }

        // Geocode search string to coordinates
        private void geocodeAndPlace(String locationName) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng found = new LatLng(address.getLatitude(), address.getLongitude());
                    selectedLocation = found;
                    mMap.addMarker(new MarkerOptions().position(found).title("Searched Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(found, 15));
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private LatLng findNearestBin(LatLng point) {
            float minDistance = Float.MAX_VALUE;
            LatLng nearest = binLocations.get(0);
            float[] result = new float[1];

            for (LatLng bin : binLocations) {
                Location.distanceBetween(point.latitude, point.longitude, bin.latitude, bin.longitude, result);
                if (result[0] < minDistance) {
                    minDistance = result[0];
                    nearest = bin;
                }
            }
            return nearest;
        }

        private void fetchRouteFromGoogle(LatLng origin, LatLng destination) {
            String apiKey = "YOUR_API_KEY"; // replace with your real API key
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                    + origin.latitude + "," + origin.longitude +
                    "&destination=" + destination.latitude + "," + destination.longitude +
                    "&mode=walking&key=" + apiKey;

            new Thread(() -> {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);

                    JSONObject response = new JSONObject(result.toString());
                    JSONArray routes = response.getJSONArray("routes");
                    if (routes.length() > 0) {
                        String encoded = routes.getJSONObject(0)
                                .getJSONObject("overview_polyline").getString("points");
                        List<LatLng> path = decodePoly(encoded);

                        runOnUiThread(() -> {
                            if (currentRoute != null) currentRoute.remove();
                            currentRoute = mMap.addPolyline(new PolylineOptions()
                                    .addAll(path).width(10).color(Color.BLUE));
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length(), lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do { b = encoded.charAt(index++) - 63; result |= (b & 0x1f) << shift; shift += 5; }
                while (b >= 0x20);
                lat += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

                shift = 0; result = 0;
                do { b = encoded.charAt(index++) - 63; result |= (b & 0x1f) << shift; shift += 5; }
                while (b >= 0x20);
                lng += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));

                poly.add(new LatLng(lat / 1E5, lng / 1E5));
            }
            return poly;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        }
    }
}
