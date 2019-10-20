
import java.net.*;
import java.io.*;

public class KnockKnockMultiServerThread extends Thread {
    private Socket socket = null;

    public KnockKnockMultiServerThread(Socket socket){
        super("KnockKnockMultiServerThread");
        this.socket = socket;
    }

    public void run(){

        try(
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        ){
            String inputLine, outputLine;
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);

            while( (inputLine = in.readLine()) != null ){
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } 
        catch (IOException oie)
        {
            oie.printStackTrace();
        }
    }
}