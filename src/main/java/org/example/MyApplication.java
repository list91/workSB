package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;


import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.*;

import javafx.scene.layout.VBox;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MyApplication extends Application {

    //    private static final String SIBG_URL = "http://server.sibgroup22.ru:8082/sdesk/hs/lk/";
    private static final String SIBG_URL = "http://server.sibgroup22.ru:2000/SdeskTest1/hs/lk/";
    //    private static final String SIBG_URL = "https://www.google.com/";

    private Label statusLabel;
    private static final String ERROR_CONTENT = """
            <html>
              <head>
                <style>
                  body {
                    background-color: #000;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: auto;
                  }

                  .box {
                    background-color: #424242;
                    text-align: center;
                    padding: 20px;
                  }

                  h2, h3 {
                    color: #fff;
                  }
                </style>
              </head>
              <body>
                <div class="box">
                  <h2>Отсутствует интернет</h2>
                  <h3>Проверьте наличие интернет соединения и перезапустите программу</h3>
                </div>
              </body>
            </html>""";

    private static void updateProgress(double progress) {
        Platform.runLater(() -> {
            String progressText = String.format("%.2f%%", progress * 100);
            System.out.println(progressText);
        });
    }

    public static String getSubstringAfterLastSpace(String input) {
        int lastIndex = input.lastIndexOf(" ");
        if (lastIndex != -1) {
            return input.substring(lastIndex + 1);
        } else {
            return input;
        }
    }



    public static void downloadFile(String fileUrl, String destinationPath, Label label) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            int contentLength = httpConn.getContentLength();

            // Получаем имя файла из заголовка Content-Disposition
            String filename = null;
            String contentDisposition = httpConn.getHeaderField("Content-Disposition");
            System.out.println(contentDisposition);
            if (contentDisposition != null && contentDisposition.indexOf("filename=") > 0) {
                filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 10, contentDisposition.length() - 1);
            }

            try {
                assert filename != null;
                String file = getSubstringAfterLastSpace(URLDecoder.decode(filename, StandardCharsets.UTF_8));


                label.setText("файл - "+file);
//            String finalFile = getSubstringAfterLastPercentTwenty(file);
                InputStream inputStream = httpConn.getInputStream();
                OutputStream outputStream = new FileOutputStream(destinationPath + "/" + file);

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                long totalBytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
//                    String allMsgs = label.getText();
                    label.setText("файл - "+file+"\nЗагрузка"+Long.toString(totalBytesRead)+" byte.");
                    totalBytesRead += bytesRead;

                    double progress = (double) totalBytesRead / contentLength;
                    updateProgress(progress);
                }

                outputStream.close();
                inputStream.close();
            } catch (UnsupportedEncodingException e) {
                System.out.println("errorName");
            }
        } else {
            throw new IOException("Failed to download file: " + httpConn.getResponseMessage());
        }

    }


    public void createScene(Stage primaryStage, WebView webView, Button button) {
        // Определяем соотношение размеров
        final double webViewHeightRatio = 0.9;
        final double statusBoxHeightRatio = 0.1;


        // Создаем метку для строки состояния и не управляем ею
        Label statusLabel = new Label("qqqq");
//        statusLabel.setManaged(false);

        // Создаем VBox для нашей сцены
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // Создаем HBox для строки состояния
        HBox statusBox = new HBox();
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        statusBox.setStyle("-fx-background-color: green;");

        // Устанавливаем привязку высоты WebView к высоте primaryStage
        webView.prefHeightProperty().bind(primaryStage.heightProperty().multiply(webViewHeightRatio));

        // Вычисляем высоту statusBox как оставшуюся часть после установки высоты WebView
        double statusBoxHeight = primaryStage.getHeight() * statusBoxHeightRatio;
        statusBox.prefHeightProperty().setValue(statusBoxHeight);

        // Разбиваем statusBox на два HBox поменьше
        HBox leftBox = new HBox();
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.getChildren().addAll(statusLabel);
        leftBox.setStyle("-fx-background-color: red;");

        HBox rightBox = new HBox();
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.getChildren().addAll(button);

        // Добавляем левый и правый ящики в statusBox
        statusBox.getChildren().addAll(leftBox, rightBox);

        // Добавляем WebView и statusBox в главный VBox
        vbox.getChildren().addAll(webView, statusBox);

        // Создаем сцену и связываем ее с primaryStage
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Окно приветствия
        VBox introBox = new VBox();
        introBox.setAlignment(Pos.BASELINE_CENTER);
        introBox.setSpacing(20);
        Label introLabel = new Label("Загрузка");
        introLabel.setFont(new javafx.scene.text.Font("Arial", 24));
        introBox.getChildren().addAll(introLabel);
        Scene introScene = new Scene(introBox, 400, 200);
        primaryStage.setScene(introScene);
        primaryStage.setTitle("Личный кабинет");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo_desctop.png"))));
        primaryStage.show();

        // Запуск Главного окна
        try {
            showMainWindow(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainWindow(Stage primaryStage) {

        // Создаем WebView(контейнер) и WebEngine(движок)
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        statusLabel = new Label("qqqq");
        statusLabel.setManaged(false);
//        webView.prefHeightProperty().bind(primaryStage.getScene().heightProperty().add(1));

        // Создаем прогресс-бар спиннер
        javafx.scene.control.ProgressIndicator progressBar = new ProgressIndicator();
        progressBar.setMaxSize(500, 500);

        // Создаем VBox для нашей сцены
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(progressBar);

        // Создаем главную сцену и добавляем вертикальныйБокс со спиннером
        Scene scene = new Scene(vbox, 800, 600);

        // Добавляем слушатель изменения размера окна и обновляем размер прогресс-бара
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            progressBar.setPrefWidth(newVal.doubleValue());
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            progressBar.setPrefHeight(newVal.doubleValue());
        });

        // На главное окно добавляю настроенную главную сцену
        primaryStage.setScene(scene);

        // ставим прослушку ожидания загрузки страницы
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {

                    // Кнопка
                    Button button = new Button("Кнопка");
                    button.setMaxHeight(Double.MAX_VALUE);

                    createScene(primaryStage, webView, button);
//                    VBox generalVbox = new VBox();
//                    generalVbox.setAlignment(Pos.CENTER);
//
//                    // Кнопка
//                    Button button = new Button("Кнопка");
//                    button.setMaxHeight(Double.MAX_VALUE);
//
//                    // Создаем ящик для строки состояния
//                    HBox statusBox = new HBox();
//                    statusBox.setAlignment(Pos.CENTER_RIGHT);
//                    statusBox.setStyle("-fx-background-color: green;");
//
//                    // Разбиваем ящик на 2 ящика поменьше
//                    // ящик слева
//                    HBox leftBox = new HBox();
//                    leftBox.setAlignment(Pos.CENTER_LEFT);
//                    leftBox.setAlignment(Pos.BASELINE_LEFT);
//                    leftBox.getChildren().addAll(statusLabel);
//                    leftBox.setStyle("-fx-background-color: red;");
//                    // ящик справа
//                    HBox rightBox = new HBox();
//                    rightBox.setAlignment(Pos.CENTER_RIGHT);
//                    rightBox.getChildren().addAll(button);
//
//                    // добавляем в статусЯщик левый и правый
//                    statusBox.getChildren().addAll(leftBox, rightBox);
//
//                    // Добавляем WebView и статусЯщик в главный ящик
//                    generalVbox.getChildren().addAll(webView, statusBox);
//
//                    // ТЕСТ
////                    statusLabel.setText("");
////                    statusLabel.setManaged(false);
////                    webView.prefHeightProperty().bind(primaryStage.getScene().heightProperty().add(-80));
//
//
//                    // Получаем ссылку на текущую сцену, связанную с основным окном приложения
//                    Scene webScene = primaryStage.getScene();
//
//                    // Устанавливается корневой узел веб-сцены на объект WebView,
//                    // который представляет загруженную веб-страницу.
//                    webScene.setRoot(generalVbox);
//
//                    // Обновляем текст строки состояния
//                    statusLabel.setText("Загрузка завершена");


//                    webView.setOnMouseClicked(event -> {
//                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
//                            webView.prefHeightProperty().bind(primaryStage.getScene().heightProperty().add(-50));
//
//                        }
//                    });

                });
            }
        });

        // Загружаем ЛК, если есть соединение. Иначе выводим текст ошибки.
        if (isWebsiteAvailable(SIBG_URL)) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    webView.getEngine().load(SIBG_URL);



//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Information Dialog");
//                    alert.setHeaderText(null);
//                    alert.setContentText("This is an example of an information dialog.");
//
//                    Button button = new Button("Hover over me");
//                    Tooltip tooltip = new Tooltip("Hello, World!");
//                    Tooltip.install(button, tooltip);


                    webView.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
//                            alert.showAndWait();
                            statusLabel.setManaged(true);
//                            webView.prefHeightProperty().bind(primaryStage.getScene().heightProperty().add(-150));
                            String selectedText = (String) webView.getEngine().executeScript("window.getSelection().toString()");
                            String currentUrl = webEngine.getLocation();

                            statusLabel.setText("Загрузка файла...");

                            try {
                                downloadFile(currentUrl, "C:/Users/Stas/IdeaProjects/untitledTest/target", statusLabel);
                                System.out.println("качаю - " + selectedText + " (" + currentUrl + ")");
                            } catch (Exception e){
                                System.out.println("ERR");
                                System.out.println(e);
                                System.out.println("User clicked the following URL: " + currentUrl);
                            }

//// Проверка, что statusLabel не равен null
//                            if (statusLabel != null) {
//                                statusLabel.setText("Файл " + file + " успешно загружен!");
//                            }


                        }
                    });

                });
            }).start();
        } else {
            Platform.runLater(() -> {
                webEngine.loadContent(ERROR_CONTENT);
            });
        }
//        new Thread(() -> {
//            Platform.runLater(() -> {
//                webView.getEngine().load(SIBG_URL);
//            });
//        }).start();


        primaryStage.setTitle("Личный кабинет");
//        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/logo_desktop.png"))));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo_desctop.png"))));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static boolean isWebsiteAvailable(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException exception) {
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}