package com.s23010255.waste_watcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SubmitSuccessActivity extends AppCompatActivity {

    private TextView pointsMessage;
    private Button returnHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitsuccess);

        pointsMessage = findViewById(R.id.pointsMessage);
        returnHomeButton = findViewById(R.id.returnHomeBtn);

        int points = getIntent().getIntExtra("total_points", 0);
        String message = "You earned " + points + " points! 🎉";
        pointsMessage.setText(message);

        returnHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SubmitSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
