package com.example;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFParser {

    private static PDDocument doc;

    /**
     * Parses a PDF file and extracts its text content.
     *
     * @param path The absolute path to the PDF file
     * @return The extracted text from the PDF
     * @throws IOException If there is an error reading the file
     */
    public static String parse(String path) throws IOException {
        try (PDDocument doc = PDDocument.load(new File(path))) { //creating in memory pdf object is neccessary to access the binary file's content
            String text =  new PDFTextStripper().getText(doc);
            closeDoc();
            return text;
        }
    }

    /**
     * Closes the currently loaded PDF document, if open.
     *
     * @throws IOException If an error occurs while closing the document
     */
    public static void closeDoc() throws IOException {
        if (doc != null) {
            doc.close();
            doc = null;
        }
    }
}

