package com.chat;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EchoClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Đường dẫn file FXML (đảm bảo file nằm đúng vị trí)
        URL fxmlLocation = getClass().getResource("/views/client_view.fxml");
        if (fxmlLocation == null) {
            System.out.println("Không tìm thấy file client_view.fxml!");
            return;
        }
        
        Parent root = FXMLLoader.load(fxmlLocation);
        primaryStage.setTitle("Echo Chat Client (TCP)");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}