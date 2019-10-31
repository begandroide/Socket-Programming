
import java.io.*;
import java.net.ServerSocket;

import Server.*;

public class Server {
    public static void main(String[] args) throws IOException {
        
        // if(args.length < 1){
        //     System.out.println("Uso: java server <ip_multicast>");
        //     System.exit(1);
        // }

        try (
            //socket del servidor en el puerto arg
            ServerSocket serverSocket = new ServerSocket(4444);
            )
            {
                ServerCommandThread sct = new ServerCommandThread(serverSocket.accept());
                sct.start();
                new MulticastServerThread("230.0.0.1",sct).start(); 
                while (true) {
                    sct =  new ServerCommandThread(serverSocket.accept());       
                    sct.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}