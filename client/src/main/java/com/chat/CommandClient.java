package com.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class CommandClient {
    public static void main(String[] args) {
        try {
            // Gửi lệnh START
            sendCommand("START");
            Thread.sleep(1000); // Đợi 1 giây
            // Gửi lệnh SHUTDOWN
            sendCommand("SHUTDOWN");
            
        } catch (ConnectException e) {
            System.err.println("Error: Remote server is offline!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendCommand(String cmd) throws IOException {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(cmd);
            System.out.println("Da gui lenh: " + cmd);
        }
    }
}