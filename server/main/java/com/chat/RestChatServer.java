package com.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class RestChatServer {
    // Quản lý ID duy nhất được chỉ định theo thứ tự tham gia
    private static final AtomicInteger clientIdCounter = new AtomicInteger(0);
    // Không gian giao tiếp chung lưu trữ tin nhắn
    private static final List<String> chatRoom = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws Exception {
        // Khởi tạo Server HTTP ở port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 1. Endpoint: /join - Đăng ký và nhận ID
        server.createContext("/join", exchange -> {
            if ("GET".equals(exchange.getRequestMethod()) || "POST".equals(exchange.getRequestMethod())) {
                int newId = clientIdCounter.incrementAndGet();
                String response = String.valueOf(newId);
                sendResponse(exchange, 200, response);
                System.out.println("Client mới đã tham gia với ID: " + newId);
            }
        });

        // 2. Endpoint: /submit - Nhận và phát sóng tin nhắn
        server.createContext("/submit", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                // Lưu tin nhắn vào phòng chung
                chatRoom.add(body);
                System.out.println("Đã nhận tin nhắn: " + body);
                
                sendResponse(exchange, 200, "OK");
            }
        });

        // 3. Endpoint: /messages - Liệt kê toàn bộ giao tiếp
        server.createContext("/messages", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Trả về toàn bộ tin nhắn, ngăn cách bởi dấu xuống dòng
                String response = String.join("\n", chatRoom);
                sendResponse(exchange, 200, response);
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("REST API Server đã khởi chạy tại http://localhost:8080/");
    }

    // Hàm hỗ trợ gửi phản hồi HTTP về cho Client
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}