package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.example.AIClients.HuggingFaceClient;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    List<String> questions = new ArrayList<>();
    List<String> answers = new ArrayList<>();
    List<String> chunks = new ArrayList<>();
    Boolean finishedProcessing = false;

    public static String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        // handle cases with no extension or multiple dots
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return ""; // no extension found
        } else {
            return fileName.substring(dotIndex + 1);
        }
    }

    // To use OllamaClient instead of HuggingFaceClient:
    // Remove all text between <think> and </think> (including the tags,
    // case-insensitive, handles whitespace/newlines)
    // answersResults =
    // answersResults.replaceAll("(?is)<think\\s*>[\\s\\S]*?</think\\s*>", "");

    /**
     * Handles file upload and processes the content to generate quiz questions.
     *
     * @param file The uploaded file, either PDF or TXT.
     * @return A ResponseEntity containing the generated quiz questions or an error
     *         message. Frontend expects a JSON array of questions (we send a list
     *         an springboot converts it to JSON
     *         automatically).
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        finishedProcessing = false;
        try {
            questions.clear();
            chunks.clear();
            String text;
            String fileExtension = getExtension(Path.of(file.getOriginalFilename()));
            if (fileExtension.equalsIgnoreCase("pdf")) {
                Path tempPath = Files.createTempFile("upload", ".pdf");
                file.transferTo(tempPath);
                text = PDFParser.parse(tempPath.toString());
                Files.delete(tempPath);
            } else if (fileExtension.equalsIgnoreCase("txt")) {
                text = new String(file.getBytes(), StandardCharsets.UTF_8);
            } else {
                return ResponseEntity.badRequest().body("Unsupported file type.");
            }
            chunks = SmartChunker.chunkText(text, 2000);
            for (String chunk : chunks) {
                String currPrompt = PromptBuilder.buildQuestionsPrompt(chunk);
                String quiestionsHuggingFace = HuggingFaceClient.generate(currPrompt);
                questions.add(quiestionsHuggingFace.trim());
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
                String answersHuggingFace = HuggingFaceClient.generate(currAnswers);
                answers.add(answersHuggingFace.trim());
            }
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating answers: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        try {
            boolean isValidUser = DBHandler.checkUserCredentials(username, password);
            System.out.println("User validation result: " + isValidUser);

            if (isValidUser) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        try {
            boolean isRegistered = DBHandler.registerUser(username, password);
            if (isRegistered) {
                return ResponseEntity.ok("Registration successful");
            } else {
                return ResponseEntity.status(400).body("Username already exists");
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/saveQuiz")
    public ResponseEntity<?> saveQuiz(@RequestBody Map<String, String> body) {
        String fileName = body.get("fileName");
        String userName = body.get("userName");
        String questions = body.get("questions");
        String answers = body.get("answers");

        System.out.println("Saving quiz with fileName: " + fileName + ", userName: " + userName);
        try {
            DBHandler.insertFile(fileName, userName, questions, answers);
            return ResponseEntity.ok("Quiz saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving quiz: " + e.getMessage());
        }
    }
}
