package com.s23010255.waste_watcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstNameTextView, emailTextViewTop, emailTextViewCard, pointsTextView;
    private Button updateProfileBtn, logoutBtn;
    private ImageView profileImageView;

    private SharedPreferences prefs;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            profileImageView.setImageBitmap(bitmap);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("profile_image_uri", selectedImageUri.toString());
                            editor.apply();

                            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstNameTextView = findViewById(R.id.firstNameText);
        emailTextViewTop = findViewById(R.id.emailTop);
        emailTextViewCard = findViewById(R.id.emailText);
        profileImageView = findViewById(R.id.profileImage);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        pointsTextView = findViewById(R.id.pointsText);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);


        String firstName = prefs.getString("first_name", "Paba");
        String email = prefs.getString("email", "abc001@gmail.com");
        String imageUri = prefs.getString("profile_image_uri", null);

        firstNameTextView.setText(firstName);
        emailTextViewTop.setText(email);
        emailTextViewCard.setText(email);


        int totalPoints = prefs.getInt("total_points", 0);
        pointsTextView.setText("Points: " + totalPoints);

        if (imageUri != null) {
            profileImageView.setImageURI(Uri.parse(imageUri));
        }

        updateProfileBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        int totalPoints = prefs.getInt("total_points", 0);
        pointsTextView.setText("Points: " + totalPoints);
    }
}
