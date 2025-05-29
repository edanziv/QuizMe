
package com.example;

import java.util.ArrayList;
import java.util.List;

public class SmartChunker {

    /**
     * Splits the input text into chunks of a given max size, trying not to break paragraphs or sentences.
     *
     * @param text The full text to split
     * @param maxChunkSize Maximum characters per chunk
     * @return List of chunks
     */
    public static List<String> chunkText(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\s*\n"); // split by empty lines

        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (paragraph.length() > maxChunkSize) {
                // Paragraph is too long, split it by sentence
                String[] sentences = paragraph.split("(?<=[.!?])\s+");
                StringBuilder subChunk = new StringBuilder();

                for (String sentence : sentences) {
                    if (subChunk.length() + sentence.length() + 1 <= maxChunkSize) {
                        subChunk.append(sentence).append(" ");
                    } else {
                        chunks.add(subChunk.toString().trim());
                        subChunk = new StringBuilder(sentence).append(" ");
                    }
                }

                if (!subChunk.isEmpty()) {
                    chunks.add(subChunk.toString().trim());
                }

            } else {
                if (currentChunk.length() + paragraph.length() + 2 <= maxChunkSize) {
                    currentChunk.append(paragraph).append("\n\n");
                } else {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder(paragraph).append("\n\n");
                }
            }
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    public static void main(String[] args) {
        String longText = "This is a long paragraph. It has multiple sentences. Each sentence should be handled properly when chunking.\n\nThis is a short paragraph.\n\nAnother very long paragraph follows. It should be split by punctuation. Sentences are better boundaries than arbitrary cuts.";
        List<String> result = chunkText(longText, 150);
        int i = 1;
        for (String chunk : result) {
            System.out.println("Chunk " + i++ + ":");
            System.out.println(chunk);
            System.out.println("-------------------------");
        }
    }
}
