package com.example.lucene.util;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

public class DemoWebSocketEmbeddings {

    private static final String WS_URL = "ws://localhost:5000/ws/embed";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static CompletableFuture<float[]> embedAsync(String text) {
        CompletableFuture<float[]> future = new CompletableFuture<>();

        try {
            WebSocketClient client = new WebSocketClient(new URI(WS_URL)) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    try {
                        send(text);  // send the text when connected
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onMessage(String message) {
                    try {
                        Map<String, Object> response = mapper.readValue(message, Map.class);
                        var rawVector = (java.util.List<Double>) response.get("embedding");
                        float[] vector = new float[rawVector.size()];
                        for (int i = 0; i < rawVector.size(); i++) {
                            vector[i] = rawVector.get(i).floatValue();
                        }
                        future.complete(vector);
                        close(); // Close after one message
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {}

                @Override
                public void onError(Exception ex) {
                    future.completeExceptionally(ex);
                }
            };

            client.connect();
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

//    public static void main(String[] args) throws Exception {
//        CompletableFuture<float[]> future = embedAsync("What's the best price for running shoes?");
//        float[] embedding = future.get();
//        System.out.println("Embedding received: size = " + embedding.length);
//    }
}
