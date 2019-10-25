
import java.io.*;

import Client.CommandClientThread;
import Client.MulticastClientThread;

public class Client {
    public static void main(String[] args) throws IOException {
        
        //230.0.0.1
        
        if(args.length < 1){
            System.out.println("Uso: java client <ip_multicast>");
            System.exit(1);
        }

        new MulticastClientThread().start(); 
        new CommandClientThread().start();       
        //TODO: implementar threads para escuchar al server
    }
}