package Server;

import java.net.*;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import Protocol.Song;

import java.io.*;

public class ServerCommandThread extends Thread {
    private boolean moreQuotes = true;
    public final int MAX_QUEUE = 7;
    public Vector<String> Queue = new Vector<String>();
    private ArrayBlockingQueue<String> bQueue;
    private ArrayBlockingQueue<Song> reproductionQueue;
    private int activeClients = 0;

    public ServerCommandThread( ArrayBlockingQueue<String> bqueue, int activeClients, ArrayBlockingQueue<Song> reproductionQueue) throws IOException {
        super("ServerCommandThread");
        this.bQueue = bqueue;
        this.activeClients = activeClients;
        this.reproductionQueue = reproductionQueue;
    }

    public void run() {
        InetAddress address;
        try(
            MulticastSocket multiSocket = new MulticastSocket(4447);
            ) 
            {
            address = InetAddress.getByName("230.0.0.1");
            multiSocket.joinGroup(address);
            while(moreQuotes){
    
                //create a MulticastSocket
    
                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                multiSocket.receive(packet);
    
                String received = new String(packet.getData(),0,packet.getLength());
                //HelloChromeCast
                if(received.compareTo("HelloChromeCast") == 0){
                    System.out.println("Nuevo cliente");
                    activeClients++;
                    byte[] activeNow = String.valueOf( activeClients ).getBytes();
                    packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
                    multiSocket.send(packet); 
                }
                if(received.compareTo("Queue") == 0){
                    System.out.println("Consulta cola");
                    byte[] activeNow = this.reproductionQueue.toString().getBytes();
                    packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
                    multiSocket.send(packet); 
                }
                if(received.contains("Queue(")){
                    System.out.println("Añade cola");
                    byte[] activeNow = this.reproductionQueue.toString().getBytes();
                    packet = new DatagramPacket(activeNow, activeNow.length,packet.getAddress(),packet.getPort());
                    multiSocket.send(packet); 
                }
                System.out.println(received);
                bQueue.add(received);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }
}