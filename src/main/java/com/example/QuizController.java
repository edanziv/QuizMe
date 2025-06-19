package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    List<String> questions = new ArrayList<>();
    List<String> answers = new ArrayList<>();
    List<String> chunks = new ArrayList<>();
    Boolean finishedProcessing = false;

    /**
     * Handles file upload and processes the content to generate quiz questions.
     *
     * @param file The uploaded file, either PDF or TXT.
     * @return A ResponseEntity containing the generated quiz questions or an error
     *         message.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        finishedProcessing = false;
        try {
            questions.clear();
            chunks.clear();
            String text;
            if (file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                Path tempPath = Files.createTempFile("upload", ".pdf");
                file.transferTo(tempPath);
                text = PDFParser.parse(tempPath.toString());
                Files.delete(tempPath);
            } else if (file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                text = new String(file.getBytes(), StandardCharsets.UTF_8);
            } else {
                return ResponseEntity.badRequest().body("Unsupported file type.");
            }
            chunks = SmartChunker.chunkText(text, 1000);
            for (String chunk : chunks) {
                String currQuestions = PromptBuilder.buildQuestionsPrompt(chunk);
                String questionsResults = OllamaClient.generate(currQuestions);
                // Remove all text between <think> and </think> (including the tags,
                // case-insensitive, handles whitespace/newlines)
                questionsResults = questionsResults.replaceAll("(?is)<think\\s*>[\\s\\S]*?</think\\s*>", "");
                questions.add(questionsResults.trim());
            }
            finishedProcessing = true;
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            finishedProcessing = true;
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/answers")
    public ResponseEntity<?> getAnswers() {
        if (!finishedProcessing) {
            List<String> error = new ArrayList<>();
            error.add("Processing is still ongoing. Please wait.");
            return ResponseEntity.status(400).body(error);
        }
        if (questions.isEmpty()) {
            List<String> error = new ArrayList<>();
            error.add("No questions available. Please upload a file first.");
            return ResponseEntity.status(400).body(error);
        }
        try {
            answers.clear();
            for (int i = 0; i < questions.size(); i++) {
                String currAnswers = PromptBuilder.buildAnswersPrompt(chunks.get(i), questions.get(i));
                String answersResults = "\n\n" + OllamaClient.generate(currAnswers);
                // Remove all text between <think> and </think> (including the tags,
                // case-insensitive, handles whitespace/newlines)
                answersResults = answersResults.replaceAll("(?is)<think\\s*>[\\s\\S]*?</think\\s*>", "");
                answers.add(answersResults.trim());
            }
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating answers: " + e.getMessage());
        }
    }

    // It configures CORS (Cross-Origin Resource Sharing) settings for your
    // application,
    // allowing your backend (localhost:8080) to accept requests from your React
    // frontend (localhost:3000).
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins("http://localhost:3000");
            }
        };
    }
}
