
import java.io.*;
import java.net.*;

public class QuoteClient {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println(
                "Usage: java QuoteClient <host_name>");
            System.exit(1);
        }
        //create a datagramSocket
        DatagramSocket dSocket = new DatagramSocket();

        //enviamos request al servidor
        byte[] buf = new byte[256];
        InetAddress iAddress = InetAddress.getByName(args[0]);

        DatagramPacket dPacket = new DatagramPacket(buf, buf.length, iAddress, 4445);
        dSocket.send(dPacket);

        //obtener respuesta
        dPacket = new DatagramPacket(buf, buf.length);
        dSocket.receive(dPacket);

        //mostrar respuesta
        String received = new String(dPacket.getData(),0,dPacket.getLength());
        System.out.println("Cita del momento: " + received);

        dSocket.close();
    }
}