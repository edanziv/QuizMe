# QuizMe

QuizMe is a full-stack Java and React application that generates exam-style questions and answers from PDF or TXT files using AI (via Ollama). 
It helps students and educators quickly create practice questions from study materials.

## Features

- Supports both PDF and TXT files as input
- Splits large documents into manageable chunks
- Uses AI (Ollama) to generate questions and answers
- REST API backend with Spring Boot
- React frontend for easy interaction
- Handles errors gracefully

## Requirements

- Java 17 or higher
- [Maven](https://maven.apache.org/)
- [Ollama](https://ollama.com/) running locally with the `deepseek-r1` model
- Internet connection (for downloading dependencies)

## Setup

1. **Clone the repository:**
   ```sh
   git clone https://github.com/YOUR_GITHUB_USERNAME/QuizMe.git
   cd QuizMe
   ```

2. **Build the project:**
   ```sh
   mvn clean package
   mvn spring-boot:run
   ```
The backend will start on [http://localhost:8080](http://localhost:8080).

3. **Start Ollama locally**  
   Make sure Ollama is running and the `deepseek-r1` model is available.

### 4. Run the frontend

```sh
cd frontend
npm install
npm start
```

The frontend will start on [http://localhost:3000](http://localhost:3000).

---

## Usage

1. Open the frontend in your browser.
2. Upload a PDF or TXT file.
3. Wait for the questions to be generated and displayed.
4. Click "Get Answers" to fetch AI-generated answers for the questions.

---

## Project Structure

```
quizme/
├── src/
│   └── main/java/com/example/
│       ├── QuizController.java
│       ├── PDFParser.java
│       ├── PromptBuilder.java
│       ├── OllamaClient.java
│       ├── SmartChunker.java
│       └── ...
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   ├── components/
│   │   │   ├── FileUpload.js
│   │   │   └── GetAnswers.js
│   │   └── styles.css
│   └── ...
├── pom.xml
└── ...
```

---

## Example

![image](https://github.com/user-attachments/assets/0d8eabb6-8833-4cc9-8f66-74c9a1826c0f)

---

**Note:**  
Make sure the Ollama server is running locally and accessible at `http://localhost:11434/api/generate` before using the application.
