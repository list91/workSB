package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class FindElem {
    public ListView<String> getAll(String filePath){
        try {
            // Загрузка HTML-файла
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            // Поиск итерируемых элементов с классом "cell grid__item"
            Elements elements = doc.getElementsByClass("cell grid__item");

            ListView<String> listView = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(); // Создаем новый объект ObservableList

            // Перебор найденных элементов
            for (Element element : elements) {
                assert false;
                items.add(element.text());
            }
            listView.setItems(items);
            return listView;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getIdInTag(String filePath, String searchString) {
        try {
            // Загрузка HTML-файла
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            // Поиск итерируемых элементов с классом "cell grid__item"
            Elements elements = doc.getElementsByClass("cell grid__item");

            // Перебор найденных элементов
            for (Element element : elements) {
                // Проверка наличия искомой строки в элементе
                if (element.text().equals(searchString)) {
                    // Возвращение id родительского тега
//                    assert element.parent() != null;
                    return element.attr("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Если строка не найдена или возникла ошибка, возвращаем null
        return null;
    }

//    public static void main(String[] args) {
//        String filePath = "путь_к_файлу.html";
//        String searchString = "TEXT";
//
//        String parentTagId = findParentTagIdWithClass(filePath, searchString);
//
//        if (parentTagId != null) {
//            System.out.println("ID родительского тега: " + parentTagId);
//        } else {
//            System.out.println("Строка не найдена или произошла ошибка.");
//        }
//    }
}
