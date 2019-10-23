
import java.io.*;
import java.net.*;

public class MulticastClient {
    public static void main(String[] args) throws IOException {
        
        //create a MulticastSocket
        MulticastSocket multiSocket = new MulticastSocket(4446);
        InetAddress address = InetAddress.getByName("230.0.0.1");
        multiSocket.joinGroup(address);

        DatagramPacket packet;

        for(int i = 0; i < 5; i++){
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            multiSocket.receive(packet);

            String received = new String(packet.getData(),0,packet.getLength());
            System.out.println("Cita del momento: " + received);
        }
        multiSocket.leaveGroup(address);
        multiSocket.close();
    }
}