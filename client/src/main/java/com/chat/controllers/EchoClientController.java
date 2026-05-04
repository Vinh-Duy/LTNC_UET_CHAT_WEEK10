package com.chat.controllers;

import java.io.*;
import java.net.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EchoClientController {
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageInput;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @FXML
    public void initialize() {
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Kết nối tới Server ở localhost, cổng 8080
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            chatArea.appendText("Đã kết nối thành công tới Server!\n");

            // Tạo một luồng riêng để lắng nghe phản hồi từ Server liên tục
            Thread readThread = new Thread(() -> {
                try {
                    String messageFromServer;
                    while ((messageFromServer = in.readLine()) != null) {
                        String finalMessage = messageFromServer;
                        // Sử dụng Platform.runLater để cập nhật giao diện JavaFX an toàn từ luồng khác
                        Platform.runLater(() -> chatArea.appendText("Server (Echo): " + finalMessage + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Mất kết nối tới server.\n"));
                }
            });
            readThread.setDaemon(true); // Thread tự đóng khi tắt ứng dụng
            readThread.start();

        } catch (IOException e) {
            chatArea.appendText("Không thể kết nối. Vui lòng bật Server trước!\n");
        }
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && out != null) {
            // Gửi tin nhắn lên Server
            out.println(message);
            
            // Hiển thị tin nhắn của chính mình lên màn hình
            chatArea.appendText("Bạn: " + message + "\n");
            
            // Xóa khung nhập
            messageInput.clear();
        }
    }
}