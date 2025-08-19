package com.s23010255.waste_watcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton, signupButton;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);
        signupButton = findViewById(R.id.signupBtn);

        db = new DatabaseHelper(this);


        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter both username and password", Toast.LENGTH_SHORT).show();
            } else if (db.checkUser(username, password)) {

                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            }
        });


        signupButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
    }
}
