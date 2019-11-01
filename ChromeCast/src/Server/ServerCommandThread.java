package Server;

import Protocol.KnockKnockProtocol;

import java.net.*;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.io.*;

public class ServerCommandThread extends Thread {
    private boolean moreQuotes = true;
    public final int MAX_QUEUE = 7;
    public Vector<String> Queue = new Vector<String>();
    private ArrayBlockingQueue<String> bQueue;

    public ServerCommandThread( ArrayBlockingQueue<String> bqueue) throws IOException {
        super("ServerCommandThread");
        this.bQueue = bqueue;
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
                String inputLine, outputLine;
    
                String received = new String(packet.getData(),0,packet.getLength());
                System.out.println(received);
                bQueue.add(received);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }
}