package Server;

import java.net.*;
import java.util.Deque;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import Protocol.Song;

import java.io.*;

public class ServerCommandThread extends Thread {
    private boolean moreQuotes = true;
    public final int MAX_QUEUE = 7;
    public Vector<String> Queue = new Vector<String>();
    private ArrayBlockingQueue<String> bQueue;
    private Deque<Song> reproductionQueue;
    private int activeClients = 0;

    public ServerCommandThread( ArrayBlockingQueue<String> bqueue, int activeClients, Deque<Song> reproductionQueue2) throws IOException {
        super("ServerCommandThread");
        this.bQueue = bqueue;
        this.activeClients = activeClients;
        this.reproductionQueue = reproductionQueue2;
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
    
                DatagramPacket packet;
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                multiSocket.receive(packet);
    
                String received = new String(packet.getData(),0,packet.getLength());

                decodeReceived(received, packet, multiSocket);
                
                System.out.println(received);
                String cmp = received.toLowerCase();
                if( (!cmp.contains("hello") && !(cmp.compareTo("queue") == 0) )) bQueue.add(received);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void decodeReceived(String received, DatagramPacket packet, MulticastSocket multiSocket) throws IOException{ 
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
    }
}