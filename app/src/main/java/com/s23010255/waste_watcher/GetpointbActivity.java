package com.s23010255.waste_watcher;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.OutputStream;

public class GetpointbActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button uploadProofButton, nextButton;
    private Uri savedImageUri;
    private RadioGroup radioGroupWasteTypes;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpointb);

        imagePreview = findViewById(R.id.imagePreviewbefore);
        uploadProofButton = findViewById(R.id.button2);
        nextButton = findViewById(R.id.nextBtn);
        radioGroupWasteTypes = findViewById(R.id.radioGroupWasteTypes);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            saveImageToAppStorage(imageUri);
                        }
                    }
                });

        uploadProofButton.setOnClickListener(view -> openImageChooser());

        nextButton.setOnClickListener(view -> {
            int selectedId = radioGroupWasteTypes.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a waste type.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (savedImageUri == null) {
                Toast.makeText(this, "Please upload a before image.", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedButton = findViewById(selectedId);
            String selectedWasteType = selectedButton.getText().toString();

            Intent intent = new Intent(this, GetpointaActivity.class);
            intent.putExtra("waste_type", selectedWasteType);
            intent.putExtra("before_image_uri", savedImageUri.toString());
            startActivity(intent);
        });
    }

    private void openImageChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 2000);
                return;
            }
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void saveImageToAppStorage(Uri sourceUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "before_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WasteWatcher");
            }

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }
                savedImageUri = uri;
                imagePreview.setImageBitmap(bitmap);
                Toast.makeText(this, "Before image saved.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission denied to access images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
