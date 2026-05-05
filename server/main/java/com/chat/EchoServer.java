package com.chat; // Nhớ sửa lại package cho đúng với của bạn nhé

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {
    private static final int PORT = 8080;
    
    // Khởi tạo bộ ghi nhật ký (Logger) theo yêu cầu của Bài 2
    private static final Logger logger = Logger.getLogger(EchoServer.class.getName());

    public static void main(String[] args) {
        logger.info("Server đang khởi động trên cổng " + PORT + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                
                // Ghi log: Trạng thái kết nối và địa chỉ IP của Client
                logger.info("Client mới đã kết nối. IP: " + clientIP);
                
                // Cấp cho mỗi Client một luồng (Thread) riêng biệt để không block Server
                new Thread(new ClientHandler(clientSocket, clientIP)).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Lỗi khi khởi động Server: ", e);
        }
    }
    
    // Lớp xử lý logic cho từng Client riêng biệt
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private String clientIP;

        public ClientHandler(Socket socket, String clientIP) {
            this.socket = socket;
            this.clientIP = clientIP;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String message;
                while ((message = in.readLine()) != null) {
                    // Ghi log: Sự kiện nhận tin nhắn từ Client
                    logger.info("Nhận từ [" + clientIP + "]: " + message);
                    
                    // Trả về đúng tin nhắn cho người gửi (Echo)
                    out.println("Server (Echo): " + message);
                    
                    // Ghi log: Sự kiện đã phản hồi
                    logger.info("Đã gửi phản hồi cho [" + clientIP + "]");
                }
            } catch (IOException e) {
                logger.warning("Kết nối bị gián đoạn từ IP: " + clientIP);
            } finally {
                try {
                    socket.close();
                    // Ghi log: Trạng thái ngắt kết nối
                    logger.info("Client [" + clientIP + "] đã ngắt kết nối.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}