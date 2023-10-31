package com.beaker.recipeRoulette;

import android.content.Context;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.util.Lists;
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
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
            throw new RuntimeException(e);
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

                    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                        annotation
                                .getAllFields()
                                .forEach((k, v) -> System.out.format("%s : %s%n", k, v.toString()));
                    }
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
