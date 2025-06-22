package com.s23010255.waste_watcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText Username, Email, password, confirmpassword;
    Button signupBtn;
    DatabaseHelper dbHelper;

    private static final String PREF_NAME = "user_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        signupBtn = findViewById(R.id.signupBtn2);

        dbHelper = new DatabaseHelper(this);

        signupBtn.setOnClickListener(view -> {
            String usernameInput = Username.getText().toString().trim();
            String emailInput = Email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmPasswordInput = confirmpassword.getText().toString().trim();

            if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(emailInput) ||
                    TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(confirmPasswordInput)) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmPasswordInput)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isUserExists(emailInput)) {
                Toast.makeText(SignUpActivity.this, "User already exists with this email", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.registerUser(usernameInput, emailInput, passwordInput);
            if (success) {


                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

