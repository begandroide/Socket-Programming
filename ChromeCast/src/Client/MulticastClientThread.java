package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;

public class MulticastClientThread extends Thread {

    protected DatagramPacket packet = null;
    protected MulticastSocket multiSocket = null;
    protected InetAddress address = null;
    private ArrayBlockingQueue<Boolean> bQueue;

    public MulticastClientThread(ArrayBlockingQueue<Boolean> bqueue) throws IOException {
        this("MulticastClientThread");
        this.bQueue = bqueue;
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
                //Si el usuario no ha tipeado supercomando para ingresar comandos
                if(this.bQueue.isEmpty()){
                    multiSocket.receive(packet);
                    String received = new String(packet.getData(),0,packet.getLength());
                    System.out.print( received + "\033[3C");
                } else{
                    sleep(1000);
                }
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
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}