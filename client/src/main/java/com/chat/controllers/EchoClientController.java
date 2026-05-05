package com.chat.controllers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EchoClientController {
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageInput;

    // Sử dụng HttpClient thay cho Socket
    private HttpClient httpClient;
    // Lưu ID của Client do Server cấp
    private int clientId = -1;

    @FXML
    public void initialize() {
        httpClient = HttpClient.newHttpClient();
        
        // 1. Tham gia phòng ngay khi mở app
        joinServer();
        
        // 2. Định kỳ lấy danh sách tin nhắn (Polling)
        startPolling();
    }

    private void joinServer() {
        try {
            // Gửi yêu cầu GET đến endpoint /join
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/join"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Server trả về ID, lưu ID này lại
            clientId = Integer.parseInt(response.body().trim());
            chatArea.appendText("Đã tham gia phòng REST API với ID: " + clientId + "\n");
        } catch (Exception e) {
            chatArea.appendText("Không thể kết nối REST Server. Vui lòng bật Server trước!\n");
        }
    }

    private void startPolling() {
        // Tạo một luồng chạy ngầm để hỏi Server liên tục (mỗi 2 giây)
        Thread pollingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000); // Đợi 2 giây
                    if (clientId != -1) {
                        // Gửi yêu cầu GET đến endpoint /messages
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/messages"))
                                .GET()
                                .build();
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        
                        // Lấy toàn bộ lịch sử chat từ Server
                        String allMessages = response.body();
                        
                        // Cập nhật giao diện an toàn
                        Platform.runLater(() -> {
                            // Ghi đè toàn bộ khu vực chat bằng danh sách tin nhắn mới nhất
                            if (!allMessages.isEmpty()) {
                                chatArea.setText("Đã tham gia phòng REST API với ID: " + clientId + "\n\n" + allMessages + "\n");
                                chatArea.setScrollTop(Double.MAX_VALUE); // Cuộn xuống cuối
                            }
                        });
                    }
                } catch (Exception e) {
                    // Nếu lỗi mạng tạm thời thì bỏ qua, đợi 2 giây sau thử lại
                }
            }
        });
        pollingThread.setDaemon(true); // Thread tự đóng khi tắt ứng dụng
        pollingThread.start();
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && clientId != -1) {
            try {
                // Gắn thẻ ID vào nội dung tin nhắn
                String fullMessage = "Client " + clientId + ": " + message;
                
                // Gửi POST lên /submit
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/submit"))
                        .POST(HttpRequest.BodyPublishers.ofString(fullMessage))
                        .build();
                
                // Gửi đi (dùng sendAsync để giao diện không bị giật/lag khi gửi)
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                
                // Xóa khung nhập
                messageInput.clear();
            } catch (Exception e) {
                chatArea.appendText("Lỗi gửi tin nhắn.\n");
            }
        }
    }
}