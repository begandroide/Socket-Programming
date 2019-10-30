
import java.io.*;
import Server.*;

public class Server {
    public static void main(String[] args) throws IOException {
        
        if(args.length < 1){
            System.out.println("Uso: java server <ip_multicast>");
            System.exit(1);
        }

        ServerCommandThread sct =  new ServerCommandThread();       
        sct.start();
        new MulticastServerThread(args[0],sct).start(); 
    }
}