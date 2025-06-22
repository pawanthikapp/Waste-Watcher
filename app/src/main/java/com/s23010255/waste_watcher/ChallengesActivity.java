package com.s23010255.waste_watcher;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {

    Button btnJoinChallenge1, btnJoinChallenge2, btnJoinChallenge3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Initialize buttons

        btnJoinChallenge2 = findViewById(R.id.btnJoinChallenge2);
        btnJoinChallenge3 = findViewById(R.id.btnJoinChallenge3);

        // Handle Join button clicks
        btnJoinChallenge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }
}