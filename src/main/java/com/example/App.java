package com.example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try (Scanner scanner = new Scanner(System.in)) {// Scanner is created here and closed automatically
            while(true){
                System.out.print("\nEnter the absolute path to the file (PDF or TXT), or type 'exit' to quit: ");
                String text = "";
                try{
                    String filePath = scanner.nextLine();
                    if (filePath.equalsIgnoreCase("exit")) {
                        System.out.println("Goodbye!");
                        break;
                    }
                    if (!Files.exists(Paths.get(filePath))) {
                        System.out.println("File does not exist: " + filePath);
                        continue;
                    }
                    if (filePath.toLowerCase().endsWith(".pdf")) {
                        System.out.println("parsing pdf file...");
                        text = PDFParser.parse(filePath);
                        PDFParser.closeDoc(); // Close the PDF document after parsing
                    } else if (filePath.toLowerCase().endsWith(".txt")) {
                        text = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
                    } else {
                        System.out.println("Unsupported file type. Please provide a PDF or TXT file.");
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Failes to parse file.");
                    System.err.println("Error: " + e.getMessage());
                    continue;
                }

                List<String> chunks = SmartChunker.chunkText(text, 500);
                for (String chunk : chunks) {
                    String prompt = PromptBuilder.buildPrompt(chunk);
                    System.out.println("\nPreperring you questions, this may take a minute...");
                    String output = "";
                    try{
                        output = OllamaClient.generate(prompt);
                    }
                    catch (Exception e) {
                        System.out.println("Failed to generate questions and answers.");
                        System.err.println("Error: " + e.getMessage());
                        return;
                    }
                    System.out.println("\n=== Questions and Answers ===");
                    System.out.println(output);

                    System.out.print("\nPress Enter to for more questions...");
                    scanner.nextLine(); // Use the same scanner
                }
                System.out.println("\nCongratulations! You have answered all the questions and are ready for the exam!");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during processing.");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
