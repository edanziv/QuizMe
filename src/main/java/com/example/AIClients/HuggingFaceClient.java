package com.example.AIClients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class HuggingFaceClient {
    private static final String HuggingFace_URL = "https://router.huggingface.co/novita/v3/openai/chat/completions";
    private static final String TOKEN = "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";

    /**
     * Sends a prompt to the Ollama API and returns the generated response.
     *
     * @param prompt The input prompt for the Ollama model
     * @return The generated response from the model
     * @throws IOException          If there is an error during the HTTP request
     * @throws InterruptedException If the request is interrupted
     */
    // since the method throws exceptions, it is necessary to add 'throws
    // IOException, InterruptedException' to the method signature
    public static String generate(String prompt) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        JsonObject payload = new JsonObject();
        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();

        payload.addProperty("model", "deepseek/deepseek-v3-0324");

        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);

        messages.add(userMessage);
        payload.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HuggingFace_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + TOKEN)
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
            if (json.has("choices")) {
                JsonArray choices = json.getAsJsonArray("choices");
                if (choices.size() > 0) {
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();
                    if (firstChoice.has("message")) {
                        JsonObject message = firstChoice.getAsJsonObject("message");
                        if (message.has("content")) {
                            return message.get("content").getAsString();
                        }
                    }
                }
            }
            return json.toString(); // fallback: return the whole response if structure is unexpected
        } catch (JsonSyntaxException | IOException | InterruptedException | IllegalArgumentException e) {
            System.err.println("Error during Hugging Face request: " + e.getMessage());
            throw e; // rethrow to handle it in the calling code
        }
    }
}
