package com.s23010255.waste_watcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.card.MaterialCardView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    MaterialCardView cardChallenges, cardCommunity, cardProfile, cardMap, cardPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize cards
        cardChallenges = findViewById(R.id.cardChallenges);
        cardCommunity = findViewById(R.id.cardCommunity);
        cardProfile = findViewById(R.id.cardProfile);
        cardMap = findViewById(R.id.cardMap);
        cardPoints = findViewById(R.id.cardPoints);

        // Set click listeners
        cardMap.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MapActivity.class)));

        cardPoints.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, GetpointintroActivity.class)));

        cardChallenges.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ChallengesActivity.class)));

        cardCommunity.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CommunityActivity.class)));

        cardProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

    }
}
