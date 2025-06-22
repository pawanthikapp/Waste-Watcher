package com.s23010255.waste_watcher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    EditText searchCommunity;
    ListView communityListView;
    Button addCommunityBtn;

    ArrayList<String> communities;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        searchCommunity = findViewById(R.id.searchCommunity);
        communityListView = findViewById(R.id.communityListView);
        addCommunityBtn = findViewById(R.id.addCommunityBtn);

        communities = new ArrayList<>();
        communities.add("Beach Cleanup Volunteers");
        communities.add("Plastic-Free Sri Lanka");
        communities.add("Recyclers Hub");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, communities);
        communityListView.setAdapter(adapter);

        // Search filter
        searchCommunity.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        // Add new community
        addCommunityBtn.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("New Community")
                    .setMessage("Enter community name:")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String newCommunity = input.getText().toString().trim();
                        if (!newCommunity.isEmpty()) {
                            communities.add(newCommunity);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Community added!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Open chat
        communityListView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapter.getItem(position);
            Intent intent = new Intent(CommunityActivity.this, ChatActivity.class);
            intent.putExtra("communityName", selected);
            startActivity(intent);
        });
    }
}
