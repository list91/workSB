package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.*;

import javafx.scene.layout.VBox;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Objects;

public class MyApplication extends Application {

    //        private static final String SIBG_URL = "http://server.sibgroup22.ru:8082/sdesk/hs/lk/";
    private static final String SIBG_URL = "http://server.sibgroup22.ru:2000/SdeskTest1/hs/lk/";
    //    private static final String SIBG_URL = "https://hitster.fm/eminem/mockingbird";
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
            String substring = input.substring(lastIndex + 1);
            return substring;
        } else {
            return input;
        }
    }



    public static void downloadFile(String fileUrl, String destinationPath, VBox downloadNode) throws IOException {
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
                String file = getSubstringAfterLastSpace(URLDecoder.decode(filename, "UTF-8"));

                InputStream inputStream = httpConn.getInputStream();
                OutputStream outputStream = new FileOutputStream(destinationPath + "/" + file);

                Platform.runLater(() -> {
                    Label downloadLabel = new Label("Загрузка файла: " + file);
                    ProgressBar downloadProgressBar = new ProgressBar();
                    downloadProgressBar.setProgress(0);

                    VBox vbox = new VBox(downloadLabel, downloadProgressBar);
                    vbox.setAlignment(Pos.CENTER_LEFT);
                    vbox.setSpacing(5);
                    downloadNode.getChildren().add(vbox);
                });

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                long totalBytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    double progress = (double) totalBytesRead / contentLength;
                    updateProgress(progress);

                    Platform.runLater(() -> {
                        downloadNode.getChildren()
                                .stream()
                                .filter(node -> node instanceof VBox)
                                .map(node -> (VBox) node)
                                .filter(vbox -> vbox.getChildren().size() == 2)
                                .filter(vbox -> ((Label) vbox.getChildren().get(0)).getText().equals("Загрузка файла: " + file))
                                .findFirst()
                                .ifPresent(vbox -> ((ProgressBar) vbox.getChildren().get(1)).setProgress(progress));
                    });
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


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Окно приветствия
        VBox introBox = new VBox();
        introBox.setAlignment(Pos.BASELINE_CENTER);
//        introBox.setPadding(new Insets(20));
        introBox.setSpacing(20);

        Label introLabel = new Label("Загрузка");
        introLabel.setFont(new javafx.scene.text.Font("Arial", 24));
//        Button introButton = new Button("Начать");
//        introButton.setOnAction(event -> {
//            try {
//                showMainWindow(primaryStage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        introBox.getChildren().addAll(introLabel);

        Scene introScene = new Scene(introBox, 400, 200);
        primaryStage.setScene(introScene);
        primaryStage.setTitle("Личный кабинет");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo_desctop.png"))));
        primaryStage.show();
        try {
            showMainWindow(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainWindow(Stage primaryStage) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        WebView webView = new WebView();

        // Создаем ProgressBar для отображения загрузки страницы
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);

        // Создаем VBox для ProgressBar и WebView
        VBox webBox = new VBox(progressIndicator, webView);
        webBox.setAlignment(Pos.CENTER);
        webBox.setSpacing(5);
        VBox.setMargin(webBox, new Insets(20));

        vbox.getChildren().add(webBox);

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        WebEngine webEngine = webView.getEngine();

        // Отображаем ProgressBar при загрузке страницы
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                progressIndicator.setVisible(false);
            } else {
                progressIndicator.setVisible(true);
            }
        });

        if (isWebsiteAvailable(SIBG_URL)) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    webView.getEngine().load(SIBG_URL);
                    webView.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                            String selectedText = (String) webView.getEngine().executeScript("window.getSelection().toString()");
                            String currentUrl = webEngine.getLocation();
                            try {
                                downloadFile(currentUrl, "C:/Users/Stas/IdeaProjects/untitledTest/target", vbox);
                                System.out.println("качаю - " + selectedText + " (" + currentUrl + ")");
                            } catch (IOException e) {
                                System.out.println("Ошибка при загрузке файла: " + e.getMessage());
                            }
                        }
                    });
                });
            }).start();
        } else {
            Platform.runLater(() -> {
                webEngine.loadContent(ERROR_CONTENT);
            });
        }
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
/////
package org.example;

        import javafx.application.Application;
        import javafx.application.Platform;
        import javafx.concurrent.Worker;

        import javafx.geometry.Insets;
        import javafx.geometry.Pos;
        import javafx.scene.Node;
        import javafx.scene.Scene;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import javafx.scene.control.ProgressIndicator;
        import javafx.scene.input.MouseButton;
        import javafx.scene.web.WebEngine;
        import javafx.scene.web.WebView;
        import javafx.stage.Stage;
        import javafx.scene.image.Image;

        import java.io.*;

        import javafx.scene.layout.VBox;

        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.net.URLDecoder;
        import java.util.Objects;

public class MyApplication extends Application {

    //    private static final String SIBG_URL = "http://server.sibgroup22.ru:8082/sdesk/hs/lk/";
    private static final String SIBG_URL = "http://server.sibgroup22.ru:2000/SdeskTest1/hs/lk/";
    //    private static final String SIBG_URL = "https://www.google.com/";
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
            String substring = input.substring(lastIndex + 1);
            return substring;
        } else {
            return input;
        }
    }



    public static void downloadFile(String fileUrl, String destinationPath) throws IOException {
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
                String file = getSubstringAfterLastSpace(URLDecoder.decode(filename, "UTF-8"));


//            String finalFile = getSubstringAfterLastPercentTwenty(file);
                InputStream inputStream = httpConn.getInputStream();
                OutputStream outputStream = new FileOutputStream(destinationPath + "/" + file);

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                long totalBytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Окно приветствия
        VBox introBox = new VBox();
        introBox.setAlignment(Pos.BASELINE_CENTER);
//        introBox.setPadding(new Insets(20));
        introBox.setSpacing(20);

        Label introLabel = new Label("Загрузка");
        introLabel.setFont(new javafx.scene.text.Font("Arial", 24));
//        Button introButton = new Button("Начать");
//        introButton.setOnAction(event -> {
//            try {
//                showMainWindow(primaryStage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        introBox.getChildren().addAll(introLabel);

        Scene introScene = new Scene(introBox, 400, 200);
        primaryStage.setScene(introScene);
        primaryStage.setTitle("Личный кабинет");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo_desctop.png"))));
        primaryStage.show();
        try {
            showMainWindow(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMainWindow(Stage primaryStage) {

//        try {
//            Thread.sleep(5000); // 5000 миллисекунд = 5 секунд
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // Создаем WebView и WebEngine
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Создаем прогресс-бар
        javafx.scene.control.ProgressIndicator progressBar = new ProgressIndicator();
        progressBar.setMaxSize(500, 500);

        // Создаем VBox для нашей сцены
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(progressBar);

        Scene scene = new Scene(vbox, 800, 600);

        // Добавляем слушатель изменения размера окна и обновляем размер прогресс-бара
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            progressBar.setPrefWidth(newVal.doubleValue());
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            progressBar.setPrefHeight(newVal.doubleValue());
        });

        primaryStage.setScene(scene);

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Platform.runLater(() -> {
                    Scene webScene = primaryStage.getScene();
                    webScene.setRoot(webView);
                });
            }
        });

        // Загружаем ЛК, если есть соединение. Иначе выводим текст ошибки.
        if (isWebsiteAvailable(SIBG_URL)) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    webView.getEngine().load(SIBG_URL);
                    webView.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                            String selectedText = (String) webView.getEngine().executeScript("window.getSelection().toString()");
                            String currentUrl = webEngine.getLocation();
                            // Если клик был произведен не на ссылке, то скачиваем документ по текущему URL
                            try {
                                downloadFile(currentUrl, "C:/Users/Stas/IdeaProjects/untitledTest/target");
                                System.out.println("качаю - " + selectedText + " (" + currentUrl + ")");
                            } catch (Exception e){
                                System.out.println("ERR");
                                System.out.println(e);
                                System.out.println("User clicked the following URL: " + currentUrl);
                            }

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