package com.s23010255.waste_watcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class GetpointbActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button uploadProofButton, nextButton;
    private Uri savedImageUri;

    private CheckBox cb1, cb2, cb3, cb4, cb5, cb6;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpointb);

        imagePreview = findViewById(R.id.imagePreviewbefore);
        uploadProofButton = findViewById(R.id.button2);
        nextButton = findViewById(R.id.nextBtn);

        cb1 = findViewById(R.id.radioButton6); // Hazardous
        cb2 = findViewById(R.id.radioButton3); // Biomedical
        cb3 = findViewById(R.id.radioButton2); // Electronic
        cb4 = findViewById(R.id.radioButton4); // Organic
        cb5 = findViewById(R.id.radioButton1); // Inert
        cb6 = findViewById(R.id.radioButton5); // Recyclable

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        savedImageUri = result.getData().getData();
                        imagePreview.setImageURI(savedImageUri);
                    }
                });

        uploadProofButton.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickIntent);
        });

        nextButton.setOnClickListener(v -> {
            int totalPoints = 0;
            if (cb1.isChecked()) totalPoints += 12;
            if (cb2.isChecked()) totalPoints += 10;
            if (cb3.isChecked()) totalPoints += 8;
            if (cb4.isChecked()) totalPoints += 6;
            if (cb5.isChecked()) totalPoints += 4;
            if (cb6.isChecked()) totalPoints += 2;


            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int previousPoints = prefs.getInt("total_points", 0);
            int updatedPoints = previousPoints + totalPoints;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("total_points", updatedPoints);
            editor.apply();

            Intent intent = new Intent(GetpointbActivity.this, GetpointaActivity.class);
            intent.putExtra("total_points", totalPoints);
            startActivity(intent);
        });
    }
}
