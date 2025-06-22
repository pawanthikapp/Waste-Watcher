package com.s23010255.waste_watcher;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    TextView communityTitle;
    EditText messageInput;
    Button sendBtn;
    ListView chatListView;

    ArrayList<String> messages;
    ArrayAdapter<String> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        communityTitle = findViewById(R.id.chatTitle);
        messageInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);
        chatListView = findViewById(R.id.messageListView);

        String community = getIntent().getStringExtra("communityName");
        communityTitle.setText("Chat - " + community);

        messages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        chatListView.setAdapter(chatAdapter);

        sendBtn.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                messages.add("Me: " + msg);
                chatAdapter.notifyDataSetChanged();
                messageInput.setText("");
            }
        });
    }
}
