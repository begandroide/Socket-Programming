package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;

public class MulticastClientThread extends Thread {

    protected DatagramPacket packet = null;
    protected MulticastSocket multiSocket = null;
    protected InetAddress address = null;
    private ArrayBlockingQueue<Boolean> bQueue;
    private Object lock = null;
    private String hostName = "";

    public MulticastClientThread(String hostName,ArrayBlockingQueue<Boolean> bqueue, Object lock) throws IOException {
        this("MulticastClientThread",hostName);
        this.bQueue = bqueue;
        this.lock = lock;
    }

    public MulticastClientThread(String name,String hostName) throws IOException {
        super(name);
        this.hostName = hostName;
        try {
            multiSocket = new MulticastSocket(4446);
            address = InetAddress.getByName(this.hostName);
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
                //Si el usuario no ha tipeado supercomando para ingresar comandos, reproducir
                if(this.bQueue.isEmpty()){
                    multiSocket.receive(packet);
                    String received = new String(packet.getData(),0,packet.getLength());
                    System.out.print( received + "\033[3C");
                } else{
                    //usuario solicitó bloquear escucha a ChromeCast por comandos
                    synchronized(lock){
                        lock.wait();
                        //esperamos ser notificados para reanudar
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    multiSocket.leaveGroup(address);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                multiSocket.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}