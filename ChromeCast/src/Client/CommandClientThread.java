package Client;

import java.io.*;
import java.net.*;

import Protocol.KnockKnockProtocol;

public class CommandClientThread extends Thread {
    protected DatagramSocket kkSocket = null;
    protected String hostName = "230.0.0.1";
    protected int portNumber = 0; /// para escuchar comandos

    
    public CommandClientThread(String port) throws IOException {
        super("CommandClientThread");
        this.portNumber = Integer.parseInt(port);
        // socket del cliente, conectado al server en el puerto arg
        kkSocket = new DatagramSocket(this.portNumber);
    }

    public void run() {
        try
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;
            
            byte[] messageByte = new byte[1000];
            
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            //mensaje de bienvenida
            System.out.println("Bienvenido a ChromeCast cliente: " + Thread.activeCount());
            System.out.println( kkp.processInput(null) );
            Boolean brokePipe = false;
            while(!brokePipe){
                fromUser = stdIn.readLine();

                if (fromUser != null) {
                    messageByte =  fromUser.getBytes();
                    InetAddress groupAddress = InetAddress.getByName(hostName);
            
                    DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
                    kkSocket.send(packet); 

                    System.out.println("Se ha enviado petición: "+ fromUser + " al servidor");
                }
            }
            System.out.println("Chao compare!");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + " Intenta con otro puerto ");
            System.exit(1);
        }
    }
}