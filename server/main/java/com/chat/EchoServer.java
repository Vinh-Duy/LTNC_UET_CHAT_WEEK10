package com.chat;

import java.io.*;
import java.net.*;

public class EchoServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Server đang khởi động trên cổng " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới đã kết nối: " + clientSocket.getInetAddress());

                // Xử lý mỗi client trong một luồng (Thread) riêng biệt
                new Thread(new ClientHandler(clientSocket)).start();
            } // <--- TRƯỚC ĐÓ BẠN BỊ COPY THIẾU DẤU NGOẶC NÀY!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Lớp xử lý logic Echo cho từng Client
class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            // Lắng nghe tin nhắn từ Client
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server nhận được: " + inputLine);
                // Ngay lập tức trả về chính xác tin nhắn đó cho Client
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Một Client đã ngắt kết nối.");
        }
    }
}