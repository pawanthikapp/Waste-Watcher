package com.s23010255.waste_watcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {

    LinearLayout dailyChallengeCard, weeklyChallengeCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        dailyChallengeCard = findViewById(R.id.dailyChallengeCard);
        weeklyChallengeCard = findViewById(R.id.weeklyChallengeCard);

        dailyChallengeCard.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengesActivity.this, DailyChallengeActivity.class);
            startActivity(intent);
        });

        weeklyChallengeCard.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengesActivity.this, WeeklyChallengeActivity.class);
            startActivity(intent);
        });

    }
}
