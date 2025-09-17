package com.example.AIClients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.*;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;

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

    public static List<String> extractQuestions(String aiText) {
        System.out.println("AI Output: " + aiText);
        List<String> output = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+\\. \\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(aiText);
        while (matcher.find()) {
            output.add(matcher.group(1).trim());
        }
        return output;
    }

    public static List<String> extractAnswers(String aiText) {
        System.out.println("AI Output: " + aiText);
        List<String> answers = new ArrayList<>();
        String[] lines = aiText.split("\\r?\\n");
        StringBuilder currentAnswer = new StringBuilder();
        boolean inAnswer = false;

        for (String line : lines) {
            line = line.trim();
            // Detect question heading (e.g., ### 1. **Question**)
            if (line.matches("^#{2,}\\s*\\d+\\. \\*\\*.*\\*\\*")) {
                // Save previous answer if exists
                if (currentAnswer.length() > 0) {
                    answers.add(currentAnswer.toString().trim());
                    currentAnswer.setLength(0);
                }
                inAnswer = true;
                continue; // skip the question line
            }
            // If line is a new question without ### (fallback)
            if (line.matches("^\\d+\\. \\*\\*.*\\*\\*")) {
                if (currentAnswer.length() > 0) {
                    answers.add(currentAnswer.toString().trim());
                    currentAnswer.setLength(0);
                }
                inAnswer = true;
                continue;
            }
            // If in answer, collect lines
            if (inAnswer && !line.isEmpty()) {
                if (currentAnswer.length() > 0)
                    currentAnswer.append(" ");
                // Remove leading dash and extra asterisks
                currentAnswer.append(line.replaceAll("^-\\s*", "").replace("**", "").trim());
            }
        }
        // Add last answer if exists
        if (currentAnswer.length() > 0) {
            answers.add(currentAnswer.toString().trim());
        }
        return answers;
    }

    public static List<String> generate(String prompt, Boolean questions) throws IOException, InterruptedException {
        System.out.println("prompt: " + prompt);
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
                            if (questions) {
                                return extractQuestions(message.get("content").getAsString());
                            } else {
                                return extractAnswers(message.get("content").getAsString());
                            }
                        }
                    }
                }
            }
            List<String> fallback = extractQuestions(json.toString());
            return fallback;
        } catch (JsonSyntaxException | IOException | InterruptedException | IllegalArgumentException e) {
            System.err.println("Error during Hugging Face request: " + e.getMessage());
            throw e; // rethrow to handle it in the calling code
        }
    }
}
