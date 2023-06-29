package org.example;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.List;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Window extends Application {

    final public String html = "terminal.hta";
    private Map<String, Boolean> checkedStateMap = new HashMap<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Конструктор терминала");

        // Создание кнопок
        Button button1 = new Button("Добавить");
        Button button2 = new Button("Выбрать на удаление");
        Button button3 = new Button("Запустить терминал");

        // Создание панели для кнопок
        HBox buttonsPane = new HBox();
        buttonsPane.getChildren().addAll(button1, button2, button3);
        buttonsPane.setSpacing(10);

        // Создание элемента управления для отображения текста
        Label label = new Label("Доступные элементы");

        FindElem findElem = new FindElem();

        // Создание списка со строковыми значениями
        ListView<String> listView = findElem.getAll(html);

        // Создание панели для контента с внутренней границей
        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(10));
        contentPane.setCenter(label);
        contentPane.setBottom(listView);

        // Создание основной панели
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.getChildren().addAll(contentPane, buttonsPane);

        button1.setOnAction(event -> {
            createNewWindow(primaryStage);
        });

        button3.setOnAction(event -> {
            File file = new File("terminal.hta");

            // Проверка поддержки операционной системой открытия файла
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        // Открытие файла
                        desktop.open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        button2.setOnAction(event -> {
            // Получаем отмеченные элементы
            List<String> selectedItems = listView.getItems().filtered(item -> checkedStateMap.getOrDefault(item, false));

            // Выводим список названий
            for (String item : selectedItems) {
                System.out.println(item);

            }
        });


        // Конвертируем список строк в список пользовательских элементов
        listView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CustomListItem listItem = new CustomListItem(item, primaryStage);
                    listItem.getCheckBox().setSelected(itemIsChecked(item)); // Устанавливаем состояние флажка
                    setGraphic(listItem);

                    // Обработчик изменения состояния флажка
                    listItem.getCheckBox().selectedProperty().addListener((observable, oldValue, newValue) -> {
                        checkedStateMap.put(item, newValue);
                    });
                }
            }

            private boolean itemIsChecked(String item) {
                return checkedStateMap.getOrDefault(item, false);
            }
        });




        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }
    private void createNewWindow(Stage primaryStage) {
        AtomicReference<String> text = new AtomicReference<>("");
        AtomicReference<String> app = new AtomicReference<>("");

        Stage newStage = new Stage();
        newStage.setTitle("Добавить элемент");

        // Создание меток
        Label label1 = new Label("Что запустить:");
        Label label2 = new Label("Картинка:");

        // Создание кнопок
        Button button1 = new Button("Выбрать1");
        Button button2 = new Button("Выбрать");
        Button button3 = new Button("Ок");
        Button button4 = new Button("Отмена"); // Добавленная четвертая кнопка

        button4.setOnAction(event -> {
            newStage.close();
            closeAndOpen(primaryStage);
        });
        // Создание текстового поля
        TextField textField = new TextField();

        // APP
        button1.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите исполняемый файл");
            File selectedFile = fileChooser.showOpenDialog(newStage);
            if (selectedFile != null) {
                System.out.println("Выбран файл: " + selectedFile.getAbsolutePath());
                app.set(selectedFile.getAbsolutePath());
            }
        });

        // картинка
        button2.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showOpenDialog(newStage);
            if (selectedFile != null) {
                System.out.println("Выбран файл: " + selectedFile.getAbsolutePath());
                text.set(selectedFile.getAbsolutePath());
            }
        });

        button3.setOnAction(event -> {
            String inputText = textField.getText();

            if (text.get() != null && !text.get().isEmpty() && inputText != null && !inputText.isEmpty() && app.get() != null && !app.get().isEmpty()) {
                // Здесь выполняйте необходимые действия, если все поля заполнены
//                System.out.println("Значения полей: ");
//                System.out.println("Выбран файл: " + text.get());
//                System.out.println("Текстовое поле: " + inputText);
                AddElem addElem = new AddElem();
                addElem.addElem(html, html, text.get(), inputText, app.get());
                newStage.close();
                createNewWindow(primaryStage);

            } else {
                if (text.get() == null || text.get().isEmpty()) {
                    System.out.println("Картинка не выбрана.");
                }
                if (inputText == null || inputText.isEmpty()) {
                    System.out.println("Текстовое поле не заполнено.");
                }
            }
        });

        newStage.setOnCloseRequest(event -> {
            // Код скрипта, который нужно выполнить при закрытии окна
            closeAndOpen(primaryStage);
        });



        // Создание сетки для размещения элементов
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Размещение меток и элементов в сетке
        gridPane.add(label1, 0, 0);
        gridPane.add(button1, 1, 0);
        gridPane.add(label2, 0, 1);
        gridPane.add(button2, 1, 1);
        gridPane.add(textField, 0, 2, 2, 1); // Занимает две колонки
        gridPane.add(button3, 0, 3);
        gridPane.add(button4, 1, 3);

        // Выравнивание элементов в сетке по левому краю
        GridPane.setHalignment(label1, HPos.LEFT);
        GridPane.setHalignment(label2, HPos.LEFT);

        // Создание контейнера для сетки и установка внутренних отступов
        VBox container = new VBox(gridPane);
        container.setSpacing(6); // Отступ между элементами контейнера
        container.setPadding(new Insets(6)); // Внутренние отступы контейнера

        Scene newScene = new Scene(container);
        newStage.setScene(newScene);
        newStage.showAndWait();
    }
    public void closeAndOpen(Stage primaryStage){
        primaryStage.close();
        Stage stage = new Stage();
        start(stage);
    }




}
class CustomListItem extends HBox {
    private final String value;
    private CheckBox checkBox;

    public CustomListItem(String value, Stage primaryStage) {
        this.value = value;

        Label label = new Label(value);

        Region spacer = new Region(); // Пустая упругая область
        HBox.setHgrow(spacer, Priority.ALWAYS); // Растяжение области

        Button button1 = new Button("картинка");
        Button button2 = new Button("текст");

        checkBox = new CheckBox();
        HBox.setHgrow(checkBox, Priority.NEVER);

        button1.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                System.out.println("Выбран файл: " + selectedFile.getAbsolutePath());
                // ЗДЕСЬ ЗАДАЕМ НОВУЮ КАРТИНКУ
            }
            label.setText("ййййййййй");
        });

        button2.setOnAction(event -> {
            label.setText("ййййййййй");
        });

        this.getChildren().addAll(label, spacer, button1, button2, checkBox);
        this.setSpacing(10);
    }

    public String getValue() {
        return value;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
