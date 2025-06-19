package com.example;

public class PromptBuilder {
    public static String buildQuestionsPrompt(String chunk) {
        return "Given the following text:\n\n" + chunk +
                "\n\nGenerate exam-style questions and number them";
    }

    public static String buildAnswersPrompt(String chunk, String questions) {
        return "Given the following text:\n\n" + chunk +
                "\n\nAnd the following questions on the text:\n\n" + questions +
                "\n\nGenerate answers to the questions, one answer per question, numbered accordingly.";
    }
}
