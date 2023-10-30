package com.beaker.recipeRoulette;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryVisions {

    public static void processImage(String imageUri) {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            // Read the image content from the image URI
            byte[] imgBytes = readImageBytesFromUri(imageUri);

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

    private static byte[] readImageBytesFromUri(String imageUri) {
        try {
            FileInputStream fileInputStream = new FileInputStream(imageUri);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            fileInputStream.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
