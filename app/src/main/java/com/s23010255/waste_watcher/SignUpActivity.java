package com.s23010255.waste_watcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText Username, Email, password, confirmpassword;
    Button signupBtn;
    DatabaseHelper dbHelper;

    private static final String TAG = "SIGNUP_DEBUG";

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
            String fullName = Username.getText().toString().trim();
            String emailInput = Email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmPasswordInput = confirmpassword.getText().toString().trim();


            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(emailInput)
                    || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(confirmPasswordInput)) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmPasswordInput)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Sign-Up button clicked");


            boolean success = dbHelper.registerUser(fullName, emailInput, passwordInput);

            if (success) {
                Log.d(TAG, "User registered in DB");


                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("first_name", fullName);
                editor.putString("email", emailInput);
                editor.apply();

                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "User already exists or failed to register", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
