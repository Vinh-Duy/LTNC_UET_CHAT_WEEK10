
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class CommandServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Command Server dang chay tren cong 5000...");
            // Cài đặt thời gian chờ 5 giây
            serverSocket.setSoTimeout(5000); 

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String command = in.readLine();

                    if ("START".equals(command)) {
                        System.out.println("System initialized...");
                    } else if ("SHUTDOWN".equals(command)) {
                        System.out.println("System shutdown...");
                        socket.close();
                        break; // Thoát vòng lặp khi nhận lệnh tắt
                    }
                    socket.close();
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout: Khong co tuong tac nao trong 5 giay qua.");
                }
            }
        } catch (BindException e) {
            System.err.println("Loi BindException: Cong 5000 da bi chiem boi mot tien trinh khac!");
        } catch (IOException e) {
            System.err.println("Loi IOException: " + e.getMessage());
        }
    }
}