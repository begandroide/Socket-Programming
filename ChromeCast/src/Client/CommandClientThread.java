package Client;

import java.io.*;
import java.net.*;

public class CommandClientThread extends Thread {
    protected Socket kkSocket = null;
    protected DataInputStream in = null;
    protected PrintWriter out = null;
    protected String hostName = "localhost";
    protected int portNumber = 0; /// para escuchar comandos

    
    public CommandClientThread(String port) throws IOException {
        super("CommandClientThread");
        this.portNumber = Integer.parseInt(port);

        try {
            // socket del cliente, conectado al server en el puerto arg
            kkSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new DataInputStream(kkSocket.getInputStream());
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    public void run() {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;
        String fromServer = "";

        Boolean brokePipe = false;
        while (!brokePipe) {

            try {
                while (in.available() > 0) {
                
                    System.out.println("Nuevo mensaje disponible");
                    byte[] messageByte = new byte[500];
                    int bytesRead = in.read(messageByte);
                    fromServer += new String(messageByte, 0, bytesRead);
                    System.out.println(fromServer);
                    fromServer = "";

                    fromUser = stdIn.readLine();
                    if (fromUser.equals("6")) {
                        brokePipe = true;
                        break;
                    }
                    if (fromUser != null) {
                        System.out.println("Se ha enviado petición: " + fromUser + " al servidor");
                        // enviamos al server peticion del usuario
                        out.println(fromUser);
                        out.flush();
                    }
                    fromServer = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Chao compare!");
    }
}