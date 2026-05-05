import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SensorReceiver {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(6000)) {
            System.out.println("Sensor Receiver dang lang nghe tren cong 6000...");
            byte[] buffer = new byte[1024];
            
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                String receivedData = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Du lieu thoi tiet nhan duoc: " + receivedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}