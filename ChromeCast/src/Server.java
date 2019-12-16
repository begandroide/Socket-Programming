
import Player.Player;
import Server.*;
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;


public class Server {
    public static void main(String[] args) throws IOException {
        
        // if(args.length < 1){
        //     System.out.println("Uso: java server <ip_multicast>");
        //     System.exit(1);
        // }
        
        int activeClients = 0;
        //cola de instrucciones, comunicacion entre Ambos threads.
        //el Thread ServerCommand envía instrucciones via la cola
        //bloqueante a el Thread Multicast
        ArrayBlockingQueue<String> bqueue = new ArrayBlockingQueue<String>(7,true); 
        Player player = new Player();
        try 
        {
            new MulticastServerThread("230.0.0.1",bqueue,player).start(); 
            new ServerCommandThread(bqueue,activeClients,player).start();

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}