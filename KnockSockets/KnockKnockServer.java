
import java.net.*;
import java.io.*;

public class KnockKnockServer {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockServer <port_number>");
            System.exit(1);
        }
        
        int portNumber = Integer.parseInt(args[0]);
        
        try (
            //socket del servidor en el puerto arg
            ServerSocket serverSocket = new ServerSocket(portNumber);
            //socket de un cliente
            Socket clientSocket = serverSocket.accept();
            //writer out del servidor al cliente
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
            //buffer que guarda lo que viene desde cliente
            BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine, outputLine;
            
            // Initiate conversation with client
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);
            // out.flush();
            outputLine = "";
            
            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                System.out.println(outputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye ma' friend!"))
                    break;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}