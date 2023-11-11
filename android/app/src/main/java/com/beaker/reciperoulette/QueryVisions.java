package com.beaker.reciperoulette;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class QueryVisions {

    public static void processImage(String imageUri, AppCompatActivity c) {

        List<String> l = new ArrayList<>();
        l.add("https://www.googleapis.com/auth/cloud-platform");
        GoogleCredentials credentials;
        ImageAnnotatorSettings ias;

        try {
            InputStream s = c.getAssets().open("cpen321-recipe-roulette-401802-1ca843ff111a.json");
            credentials = GoogleCredentials.fromStream(s).createScoped(l);
            ias = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        } catch (IOException e) {
            System.err.println("Failed to initialize Google Cloud Vision credentials: " + e.getMessage());
            return;
        }


        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(ias)) {
            // Read the image content from the image URI
            byte[] imgBytes = readImageBytesFromUri(imageUri, c);

            if (imgBytes != null) {
                // Build the image annotation request
                ByteString imgData = ByteString.copyFrom(imgBytes);
                Image img = Image.newBuilder().setContent(imgData).build();
                Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .setImage(img)
                        .build();
                List<AnnotateImageRequest> requests = new ArrayList<>();
                requests.add(request);

                // Perform label detection on the image
                BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        System.out.format("Error: %s%n", res.getError().getMessage());
                        return;
                    }
                    List<String> descriptions = new ArrayList<>();

                    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                        String description = annotation.getDescription(); // Get the description value from the annotation
                        descriptions.add(description);
                    }
                    SharedPreferences sharedPref =
                            c.getSharedPreferences("com.beaker.reciperoulette.TOKEN", Context.MODE_PRIVATE);
                    String tok = sharedPref.getString("TOKEN", "NOTOKEN");
                    String email = sharedPref.getString("EMAIL", "NOEMAIL");

                    IngredientsRequest ingredientsRequest = new IngredientsRequest();
                    ingredientsRequest.userId = email;
                    List<Ingredient> ingredientList = new ArrayList<>();
                    for (String description : descriptions) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.name = description;
                        ingredient.count = 1;

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        long unixTime = calendar.getTimeInMillis() / 1000;

                        ingredient.date = new long[] {unixTime};
                        ingredientList.add(ingredient);
                    }
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
                }
            } else {
                System.out.println("Failed to read image bytes from the URI.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readImageBytesFromUri(String imageUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imageUri));
            if (inputStream != null) {
                byte[] buffer = new byte[4096];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}