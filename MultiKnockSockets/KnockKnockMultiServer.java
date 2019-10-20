
import java.net.*;
import java.io.*;

public class KnockKnockMultiServer {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockMultiServer <port_number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        try (
            //socket del servidor en el puerto arg
            ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (listening) {
                new KnockKnockMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}