package com.example;

public class PromptBuilder {
    public static String buildQuestionsPrompt(String chunk) {
        return "Given the following text:\n\n" + chunk +
                "\n\nGenerate exam-style questions and number them";
    }

    public static String buildAnswersPrompt(String chunk, String questions) {
        return "Given the following text:\n\n" + chunk +
                "\n\nAnd the following questions on the text:\n\n" + questions +
                "\n\nProvide a full answer for each of the questions mentioned.";
    }

    public static String buildQuizPrompt(String questions){
        return "Given the following questions:\n\n" + questions + 
                "\n\n for each question generate a multiple choice question.";
    }
}
