package com.palmscanner.pos.threads;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPollingThread extends Thread {
    private final String requestUrl;
    private final int timeoutSeconds;
    private final Callback callback;

    public HttpPollingThread(String requestUrl, int timeoutSeconds, Callback callback) {
        this.requestUrl = requestUrl;
        this.timeoutSeconds = timeoutSeconds;
        this.callback = callback;
    }

//    public static void main(String[] args) {
//        String url = "https://api.github.com"; // Example URL
//        int timeout = 10; // 10 seconds timeout
//
//        HttpPollingThread thread = new HttpPollingThread(url, timeout, new Callback() {
//            @Override
//            public void onSuccess(String response) {
//                System.out.println("Success! Response: " + response);
//            }
//
//            @Override
//            public void onFailure(String error) {
//                System.err.println("Failed: " + error);
//            }
//        });
//
//        thread.start();
//    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        while (true) {
            try {
                // Check timeout
                if ((System.currentTimeMillis() - startTime) / 1000 >= timeoutSeconds) {
                    callback.onFailure("Timeout reached");
                    return;
                }

                // Create HTTP GET request
                HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
                conn.setRequestMethod("GET");
//                conn.setConnectTimeout(5000); // 5 seconds timeout
//                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.has("token")) {
                        String token = jsonResponse.getString("token");
                        if (!token.isEmpty()) {
                            callback.onSuccess(token);
                            return;
                        }
                    }
                }

                conn.disconnect();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            // Wait 1 second before retrying
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public interface Callback {
        void onSuccess(String token);

        void onFailure(String error);
    }
}
