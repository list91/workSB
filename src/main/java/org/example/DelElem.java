package org.example;

import java.io.*;

public class DelElem {
    boolean targetFound = false;
    public void deleteElement(String inputFile, String outputFile, String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
            String tag = "<div id='" + id + "' class=\"cell grid__item\">";

            while ((line = reader.readLine()) != null) {
                line = line + "\n";
                htmlContent.append(line);

                if (line.contains(tag)) {
                    targetFound = true;
                }
            }

            if (targetFound) {
                System.out.println("Искомый текст найден.");
            } else {
                System.out.println("Искомый текст не найден.");
            }

            String start = "<!-- START " + id + " -->";
            String end = "<!-- END " + id + " -->";

            String modifiedHTML = replaceTextInTag(start, end, htmlContent.toString(), "");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(modifiedHTML);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String replaceTextInTag(String openingTag, String closingTag, String htmlText, String replacementText) {
        int startTagIndex = htmlText.indexOf(openingTag);
        int endTagIndex = htmlText.indexOf(closingTag);

        if (startTagIndex == -1 || endTagIndex == -1) {
            return htmlText;
        }

        String tagContent = htmlText.substring(startTagIndex, endTagIndex + closingTag.length());

        return htmlText.replace(tagContent, replacementText);
    }
}
