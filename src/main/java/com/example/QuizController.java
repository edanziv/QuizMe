package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.example.AIClients.HuggingFaceClient;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    Map<Integer, List<String>> questions = new HashMap<>();
    Map<Integer, List<String>> answers = new HashMap<>();
    List<String> chunks = new ArrayList<>();
    Boolean finishedProcessing = true;

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
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                String currPrompt = PromptBuilder.buildQuestionsPrompt(chunk);
                List<String> questionsHuggingFace = HuggingFaceClient.generate(currPrompt, true);
                questions.put(i, questionsHuggingFace);
            }
            finishedProcessing = true;
            List<String> allQuestions = questions.values().stream() // gets a collection of all the lists of questions
                                                                    // (one list per chunk).
                    .flatMap(List::stream) // flattens all the lists of questions into a single stream of questions
                    .collect(Collectors.toList());
            return ResponseEntity.ok(allQuestions);
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
            for (int i = 0; i < chunks.size(); i++) {
                List<String> chunkQuestions = questions.get(i);
                if (chunkQuestions == null || chunkQuestions.isEmpty())
                    continue;
                String questionsString = String.join("\n", chunkQuestions);
                String currAnswersPrompt = PromptBuilder.buildAnswersPrompt(chunks.get(i), questionsString);
                List<String> answersHuggingFace = HuggingFaceClient.generate(currAnswersPrompt, false);
                answers.put(i, answersHuggingFace);
            }
            List<String> allAnswers = answers.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            questions.clear();
            chunks.clear();
            return ResponseEntity.ok(allAnswers);
        } catch (Exception e) {
            System.out.println("Error generating answers: " + e.getMessage());
            return ResponseEntity.status(500).body("Error generating answers: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        if (!isValidEmail(email)) {
            return ResponseEntity.status(401).body("Invalid email");
        }

        try {
            boolean isValidUser = DBHandler.checkUserCredentials(email, password);
            System.out.println("User validation result: " + isValidUser);

            if (isValidUser) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during login: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (!isValidEmail(email)) {
            return ResponseEntity.status(422).body("Invalid email");
        }

        try {
            boolean isRegistered = DBHandler.registerUser(email, password);
            if (isRegistered) {
                return ResponseEntity.ok("Registration successful");
            } else {
                return ResponseEntity.status(401).body("email already exists");
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/saveQuiz")
    public ResponseEntity<?> saveQuiz(@RequestBody Map<String, String> body) {
        String fileName = body.get("fileName");
        String email = body.get("email");
        String questions = body.get("questions");
        String answers = body.get("answers");

        System.out.println("Saving quiz with fileName: " + fileName + ", userName: " + email);
        try {
            DBHandler.insertFile(fileName, email, questions, answers);
            return ResponseEntity.ok("Quiz saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving quiz: " + e.getMessage());
        }
    }

    @PostMapping("/history")
    public ResponseEntity<?> getHistory(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (!isValidEmail(email)) {
            return ResponseEntity.status(422).body("Invalid email");
        }

        try {
            List<Map<String, String>> history = DBHandler.getFiles(email);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            System.err.println("Error retrieving history: " + e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving history: " + e.getMessage());
        }
    }
}
