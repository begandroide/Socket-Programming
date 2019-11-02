
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;

import Server.*;

public class Server {
    public static void main(String[] args) throws IOException {
        
        // if(args.length < 1){
        //     System.out.println("Uso: java server <ip_multicast>");
        //     System.exit(1);
        // }
        int activeClients = 0;
        try 
            {
                ArrayBlockingQueue<String> bqueue = new ArrayBlockingQueue<String>(7,true); 
                ArrayBlockingQueue<String> reproductionQueue = new ArrayBlockingQueue<String>(20,true); 
                reproductionQueue.add("Foyone-Presidente");
                new MulticastServerThread("230.0.0.1",bqueue,reproductionQueue).start(); 
                new ServerCommandThread(bqueue,activeClients,reproductionQueue).start();
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}