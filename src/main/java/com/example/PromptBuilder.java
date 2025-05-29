package com.example;

public class PromptBuilder {
    public static String buildPrompt(String chunk) {
        return "Given the following text:\n\n" + chunk +
               "\n\nGenerate up to 5 exam-style questions and after every question write the answer from the text.";
    }
}

