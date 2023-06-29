package org.example;

import java.io.*;
import java.util.Random;

public class AddElem {
    public void addElem(String inputFile, String outputFile, String srcImg, String content, String runApp){
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line + "\n";
                htmlContent.append(line);
            }

            String runAppFormat = runApp.replace("\\", "\\\\");
            String id = generateRandomText(18);
            String start = "<!-- START " + id + " -->";
            String end = "<!-- END " + id + " -->\n<!-- ADD ELEMENT -->";
            String maket = "<div id='"+ id +"' class=\"cell grid__item\">\n" +
                                    "<img class=\"icon\" src='"+srcImg+"'>\n" +
                                    "<div class=\"prog_title\">"+content+"</div>\n</div>";
            maket = start + "\n" + maket + "\n" + end;


            String modifiedHTML = htmlContent.toString().replace("<!-- ADD ELEMENT -->", maket);

            String codeJS = "// " +start+"\n"+
                    "// "+content+"\n" +
                    "var button_"+id+"  = document.getElementById('"+id+"');\n" +
                    "\n" +
                    "function launch_"+id+"()\n" +
                    "{\n" +
                    "   var oShell = new ActiveXObject(\"WScript.Shell\");\n" +
                    "   oShell.Run('\"' + '"+runAppFormat+"'  + '\"', 1);\n" +
                    "}\n" +
                    "button_"+id+".addEventListener(\"click\", launch_"+id+");\n" +
                    "// <!-- END " + id + " -->";
            addCodeJS("script/script.js", codeJS);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(modifiedHTML);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void addCodeJS(String fileName, String textToAppend) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.append(textToAppend);
            writer.newLine();
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
