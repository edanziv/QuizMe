package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class OllamaClient {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public static String generate(String prompt) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "deepseek-r1");
        payload.addProperty("prompt", prompt);
        payload.addProperty("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // BodyHandler
                                                                                                        // tells the
                                                                                                        // client to
                                                                                                        // read the
                                                                                                        // response as a
                                                                                                        // string
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return json.has("response") ? json.get("response").getAsString() : json.toString();
        } catch (JsonSyntaxException | IOException | InterruptedException | IllegalArgumentException e) {
            System.err.println("Error during Ollama request: " + e.getMessage());
            throw e; // rethrow to handle it in the calling code
        }
    }
}
