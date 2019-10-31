package Client;

import java.io.*;
import java.net.*;

public class MulticastClientThread extends Thread {

    protected DatagramPacket packet = null;
    protected MulticastSocket multiSocket = null;
    protected InetAddress address = null;

    public MulticastClientThread() throws IOException {
        this("MulticastClientThread");
    }

    public MulticastClientThread(String name) throws IOException {
        super(name);
        try {
            multiSocket = new MulticastSocket(4446);
            address = InetAddress.getByName("230.0.0.1");
            multiSocket.joinGroup(address);
        } catch (Exception e) {
            System.out.println("Error en constructor del thread");
        }
    }

    public void run() {

        while (true) {
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            try {
                multiSocket.receive(packet);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                try {
                    multiSocket.leaveGroup(address);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                multiSocket.close();
            }
            String received = new String(packet.getData(),0,packet.getLength());
            System.out.print( received );
        }
    }
}