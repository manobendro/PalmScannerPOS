package com.palmscanner.pos.utils;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiHelper {

    public static void postData(String endpoint, String xAuthToken, String jsonData, Callback callback) {
        new Thread(() -> {
            try {
                HttpURLConnection conn = getHttpURLConnection(endpoint, xAuthToken, jsonData);

                // Read response
                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    callback.onSuccess("Request successful: " + responseCode);
                } else {
                    callback.onFailure("Error: HTTP " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                callback.onFailure("Exception: " + e.getMessage());
            }
        }).start();
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String endpoint, String xAuthToken, String jsonData) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set request properties
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-auth-token", xAuthToken);
        conn.setDoOutput(true);

        // Write JSON data to request body
        if(jsonData != null) {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        return conn;
    }

    public interface Callback {
        void onSuccess(String response);

        void onFailure(String error);
    }
}
