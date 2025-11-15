# QuizMe

QuizMe is a full-stack Java and React application that generates exam-style questions and answers from PDF or TXT files using AI (via HuggingFace or Ollama). 
It helps students and educators quickly create practice questions from study materials.

## Features

- Supports both PDF and TXT files as input
- Splits large documents into manageable chunks
- Uses AI (HuggingFace or Ollama) to generate questions and answers
- Saves generated questions and answers in a MySQL database
- User authentication system: create an account with email and password, also saved in the DB
- REST API backend with Spring Boot
- React frontend for easy interaction
- Handles errors gracefully

## Requirements

- Java 17 or higher
- [Maven](https://maven.apache.org/)
- [Ollama](https://ollama.com/) running locally with the `deepseek-r1` model
- [Hugging Face API key](https://huggingface.co/settings/tokens)
- Internet connection (for downloading dependencies)

## Setup

1. **Clone the repository:**
   ```sh
   git clone https://github.com/YOUR_GITHUB_USERNAME/QuizMe.git
   cd QuizMe
   ```

2. **Choose an AI engine:**

   HuggingFace engine:
   Configure your HuggingFace API token and add it to HuggingFaceClient.java

   Ollama local engine:
   Make sure Ollama is running and the `deepseek-r1` model is available.

3. **Configure MySQL DB:**

   Crate a database on MySQL
   Update your credentials and DB URL on DBHandler.java 

4. **Build the project:**
   ```sh
   mvn clean package
   mvn spring-boot:run
   ```
The backend will start on [http://localhost:8080](http://localhost:8080).

 5. **Run the frontend:**

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
5. Save the results to the DB by clicking "Save Results"
6. View quizs history in the History page

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
