package Server;

import java.net.*;

import Protocol.KnockKnockProtocol;

import java.io.*;

public class ServerCommandThread extends Thread {

    private boolean moreQuotes = true;

    public ServerCommandThread() throws IOException{
        super("ServerCommandThread");
    }

    public void run(){
        while(moreQuotes){
            try 
            (
                //socket del servidor en el puerto arg
                ServerSocket serverSocket = new ServerSocket(4445);
                //socket de un cliente
                Socket clientSocket = serverSocket.accept();
                //writer out del servidor al cliente
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
                //buffer que guarda lo que viene desde cliente
                BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            ) 
            {
                String inputLine, outputLine;
            
                // Initiate conversation with client
                KnockKnockProtocol kkp = new KnockKnockProtocol();
                outputLine = kkp.processInput(null);
                out.println(outputLine);
                out.flush();
                outputLine = "";
                
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Ha llegado: "+ inputLine + " desde el cliente");
                    outputLine = kkp.processInput(inputLine);
                    System.out.println("Petición procesada -> :" + outputLine);
                    out.println(outputLine);
                    System.out.println("Respuesta enviada -> :" + outputLine);
                    outputLine = "";
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
    }

}