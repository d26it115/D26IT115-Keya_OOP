import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LogAnalyzer {
    public static void main(String[] args) {

        String keyword = "ERROR";

        Path[] files = {
                Paths.get("log1.txt"),
                Paths.get("log2.txt"),
                Paths.get("log3.txt")
        };

        int totalLines = 0;
        int keywordLines = 0;

        for (Path file : files) {

            try {
                long size = Files.size(file);

                System.out.println("File: " + file.getFileName());
                System.out.println("Size: " + size + " bytes");

                List<String> lines = Files.readAllLines(file);

                for (String line : lines) {
                    totalLines++;

                    if (line.contains(keyword)) {
                        keywordLines++;
                    }
                }

                System.out.println();

            } catch (IOException e) {
                System.out.println("Error reading " + file.getFileName());
            }
        }

        System.out.println("Total lines: " + totalLines);
        System.out.println("Lines containing \"" + keyword + "\": " + keywordLines);
    }
}