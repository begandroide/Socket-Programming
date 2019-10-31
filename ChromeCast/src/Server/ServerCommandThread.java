package Server;

import Protocol.KnockKnockProtocol;

import java.net.*;
import java.util.Vector;
import java.io.*;

public class ServerCommandThread extends Thread {
    private Socket socket = null;
    private boolean moreQuotes = true;
    public final int MAX_QUEUE = 7;
    public Vector<String> Queue = new Vector<String>();

    public ServerCommandThread(Socket socket) throws IOException{
        super("ServerCommandThread");
        this.socket = socket;
    }

    public void run(){
        while(moreQuotes){
            try 
            (
                //writer out del servidor al cliente
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);                   
                //buffer que guarda lo que viene desde cliente
                BufferedReader in = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
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
                    putMessage(inputLine);
                    // System.out.println("Petición procesada -> :" + outputLine);
                    out.println(outputLine);
                    // System.out.println("Respuesta enviada -> :" + outputLine);
                    outputLine = "";
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private synchronized void putMessage(String command) throws InterruptedException {
        if(Queue.size() == MAX_QUEUE) return;
        Queue.addElement(command);
        notify();
    }

    public synchronized String getMessage() throws InterruptedException {
        notify();
        if(Queue.size() == 0) return "";

        String mss = (String)Queue.firstElement();
        Queue.removeElement(mss);
        return mss;
    }
}