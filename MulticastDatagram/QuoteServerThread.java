
import java.net.*;
import java.util.Date;
import java.io.*;

public class QuoteServerThread extends Thread {
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected Boolean moreQuotes = true;

    public QuoteServerThread() throws IOException{
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

        try {
            in =  new BufferedReader(new FileReader("one-liniers.txt"));            
        } catch (FileNotFoundException e) {
            System.err.println("No podemos abrir el archivo. Server time en cambio.");
        }
    }

    public void run(){
        while(moreQuotes){
            try {
                byte[] buf = new byte[256];
                //recibir datagrama
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //resolver y enviar respuesta
                String dString = null;
                if(in == null){
                    dString = new Date().toString();
                } else{
                    dString = getNextQuote();
                }

                buf = dString.getBytes();

                //debemos enviar la respuesta al cliente
                // se debe saber dirección del cliente y puerto desde donde llegó 
                // la request
                InetAddress iAddress = packet.getAddress();
                int port  = packet.getPort();
                packet = new DatagramPacket(buf, buf.length,iAddress,port);
                socket.send(packet); 
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
    }

    protected String getNextQuote(){
        String returnValue = null;
        try {
            if((returnValue = in.readLine()) == null){
                in.close();
                moreQuotes = false;
                returnValue = "No more Quotes, Good Bye";
            }
        } catch (IOException e) 
        {
            returnValue = "IOException en obtener nueva cita en servidor";
        }
        return returnValue;
    }

}