
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        
        if(args.length < 1){
            System.out.println("Uso: java client <ip_multicast>");
            System.exit(1);
        }

        //TODO: implementar threads para comandos y escuchar al server
        // new ServerCommandThread().start();       
        // new MulticastServerThread(args[0]).start(); 
    }
}