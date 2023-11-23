package com.beaker.reciperoulette;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TakePhoto extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int GALLERY_PERMISSION_CODE = 1001;
    Button mCaptureBtn;
    Button mSelectPicButton;
    Button mManualBtn;
    ImageView mImageView;

    private boolean imageSelectedOrCaptured = false; // Flag to track if an image has been selected or captured
    private Button sendImageBtn;

    Uri image_uri; // This will carry the resulting photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);

        mImageView = findViewById(R.id.imageview);
        mCaptureBtn = findViewById(R.id.capture_image_btn);
        mManualBtn = findViewById(R.id.manual_entry_btn);
        mSelectPicButton = findViewById(R.id.select_image_btn);

        sendImageBtn = findViewById(R.id.send_image_btn);
        sendImageBtn.setVisibility(View.GONE);

        mSelectPicButton.setOnClickListener(view -> {
            if (checkGalleryPermissions()) {
                openGallery();
            } else {
                requestGalleryPermissions();
            }
        });

        mCaptureBtn.setOnClickListener(view -> {
            if (checkPermissions()) {
                openCamera();
            } else {
                requestPermissions();
            }
        });

        mManualBtn.setOnClickListener(view -> showManualEntryDialog());

        sendImageBtn.setOnClickListener(view -> {
            if (imageSelectedOrCaptured) {
                String imageUriString = image_uri.toString();
                QueryVisions.processImage(imageUriString, TakePhoto.this);

                Intent returnMenuIntent = new Intent(this, MainMenu.class);
                startActivity(returnMenuIntent);
            } else {
                Toast.makeText(TakePhoto.this, "Please capture or select an image first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkGalleryPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermissions() {
        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permission, GALLERY_PERMISSION_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someGalleryActivityResultLauncher.launch(galleryIntent);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        String[] permission = {Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permission, PERMISSION_CODE);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        someActivityResultLauncher.launch(cameraIntent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mImageView.setImageURI(image_uri);
                        imageSelectedOrCaptured = true; // Set the flag to true when an image is captured
                        sendImageBtn.setVisibility(View.VISIBLE); // Show the button
                    } else {
                        Toast.makeText(TakePhoto.this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> someGalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the selected image URI
                        Uri selectedImageUri = Objects.requireNonNull(result.getData()).getData();
                        image_uri = selectedImageUri;
                        mImageView.setImageURI(selectedImageUri);
                        imageSelectedOrCaptured = true; // Set the flag to true when an image is captured
                        sendImageBtn.setVisibility(View.VISIBLE); // Show the button
                    } else {
                        Toast.makeText(TakePhoto.this, "Failed to select image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissions denied. Cannot capture photo.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Gallery permissions denied. Cannot select a photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showManualEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Grocery Item");

        // Create an EditText for user input
        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Get the entered grocery item from the EditText
            String groceryItem = editText.getText().toString();

            SharedPreferences sharedPref =
                    getSharedPreferences("com.beaker.reciperoulette.TOKEN", Context.MODE_PRIVATE);
            String tok = sharedPref.getString("TOKEN", "NOTOKEN");
            String email = sharedPref.getString("EMAIL", "NOEMAIL");

            IngredientsRequest ingredientsRequest = new IngredientsRequest();
            ingredientsRequest.userId = email;
            List<Ingredient> ingredientList = new ArrayList<>();
            Ingredient ingredient = new Ingredient();
            ingredient.name = groceryItem;
            ingredient.count = 1;

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            long unixTime = calendar.getTimeInMillis() / 1000;
            ingredient.date = new long[] {unixTime};

            ingredientList.add(ingredient);
            ingredientsRequest.ingredients = ingredientList;

            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(gson.toJson(ingredientsRequest), JSON); // Convert the descriptions array to a JSON string

            String acceptUrl = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/foodInventoryManager/upload";

            Request req = new Request.Builder()
                    .url(acceptUrl)
                    .addHeader("userToken", tok)
                    .addHeader("email", email)
                    .post(body)
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.err.println("Request failed with code: " + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    System.out.println("Response: " + responseBody);
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}

