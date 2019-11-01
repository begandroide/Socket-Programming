
import java.io.*;

import Client.CommandClientThread;
import Client.MulticastClientThread;

public class Client {
    
    public static void main(String[] args) throws IOException {

        //230.0.0.1
        
        if(args.length < 1){
            System.out.println("Uso: java client <port>");
            System.exit(1);
        }

        new MulticastClientThread().start();
        new CommandClientThread(args[0]).start();
    }
}