# QuizMe

QuizMe is a Java application that generates exam-style questions and answers from PDF or TXT files using AI (via Ollama). It helps students and educators quickly create practice questions from study materials.

## Features

- Supports both PDF and TXT files as input
- Splits large documents into manageable chunks
- Uses AI (Ollama) to generate up to 5 questions and answers per chunk
- Interactive command-line interface
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
   ```

3. **Start Ollama locally**  
   Make sure Ollama is running and the `deepseek-r1` model is available.

## Usage

1. **Run the application:**
   ```sh
   mvn exec:java -Dexec.mainClass="com.example.App"
   ```

2. **Follow the prompts:**
   - Enter the absolute path to a PDF or TXT file.
   - Wait for the questions to be generated.
   - Press Enter to get more questions or type `exit` to quit.

## Project Structure

```
src/
  main/
    java/com/example/
      App.java
      PDFParser.java
      PromptBuilder.java
      OllamaClient.java
      SmartChunker.java
      TextSplitter.java
  test/
    java/com/example/
    resources/
pom.xml
```

## Example

```
Enter the absolute path to the file (PDF or TXT), or type 'exit' to quit: /path/to/French_Revolution.txt

Preperring you questions, this may take a minute...

=== Questions and Answers ===
1. What event marked the end of absolute monarchy in France?
   Answer: The French Revolution.

...

Press Enter to for more questions...
```
---

**Note:**  
Make sure the Ollama server is running locally and accessible at `http://localhost:11434/api/generate` before using the application.
