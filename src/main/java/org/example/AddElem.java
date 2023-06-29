package org.example;

import java.io.*;
import java.util.Random;

public class AddElem {
    public void addElem(String inputFile, String outputFile, String srcImg, String content){
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
//            String tag = "<div id='" + id + "' class=\"cell grid__item\">";

            while ((line = reader.readLine()) != null) {
                line = line + "\n";
                htmlContent.append(line);
            }

            String id = generateRandomText(18);
            String start = "<!-- START " + id + " -->";
            String end = "<!-- END " + id + " -->\n<!-- ADD ELEMENT -->";
            String maket = "<div id='"+ id +"' class=\"cell grid__item\">\n" +
                                    "<img class=\"icon\" src='"+srcImg+"'>\n" +
                                    "<div class=\"prog_title\">"+content+"</div>\n</div>";
            maket = start + "\n" + maket + "\n" + end;


            String modifiedHTML = htmlContent.toString().replace("<!-- ADD ELEMENT -->", maket);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(modifiedHTML);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String generateRandomText(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
