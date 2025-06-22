package com.s23010255.waste_watcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView;
    private Button editProfileBtn, historyBtn, logoutBtn;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        profileImageView = findViewById(R.id.profileImageView);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        historyBtn = findViewById(R.id.historyBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Get stored user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("name", "Your Name");
        String email = prefs.getString("email", "user@example.com");

        // Set data to views
        nameTextView.setText(userName);
        emailTextView.setText(email);

        // Set click listeners (optional)
        editProfileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, EditProfileActivity.class)); // If needed
        });

        historyBtn.setOnClickListener(v -> {
            Toast.makeText(this, "View Cleanup History clicked", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            // Clear SharedPreferences if logging out
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            finish();
        });
    }
}
