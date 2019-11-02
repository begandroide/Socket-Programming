package Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import Protocol.KnockKnockProtocol;

public class CommandClientThread extends Thread {
    protected DatagramSocket kkSocket = null;
    protected String hostName = "230.0.0.1";
    protected int portNumber = 0; /// para escuchar comandos
    public int clientID = 0;
    protected List<String> historyCommands = null;
    private ArrayBlockingQueue<Boolean> bQueue;
    
    public CommandClientThread(String port, ArrayBlockingQueue<Boolean> bqueue) throws IOException {
        super("CommandClientThread");
        this.portNumber = Integer.parseInt(port);
        // socket del cliente, conectado al server en el puerto arg
        kkSocket = new DatagramSocket(this.portNumber);
        this.historyCommands = new ArrayList<String>();
        this.bQueue = bqueue;
    }
    
    public void run() {
        try
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;
            
            byte[] messageByte = new byte[1000];
            
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            //mensaje de bienvenida

            messageByte =  "HelloChromeCast".getBytes();
            InetAddress groupAddress = InetAddress.getByName(hostName);
    
            DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            kkSocket.send(packet); 

            //esperar id de cliente de servidor
            kkSocket.receive(packet);

            String received = new String(packet.getData(),0,packet.getLength());

            this.clientID = Integer.parseInt(received);
            System.out.println("Bienvenido a ChromeCast cliente: " + this.clientID);
            System.out.println("ESPACIO y ENTER para enviar comando a servidor");
            System.out.println( kkp.processInput(null) );

            Boolean brokePipe = false;
            while(!brokePipe){
                
                fromUser = stdIn.readLine();

                if (fromUser != null) {
                    if(fromUser.compareTo(" ") == 0){
                        System.out.println("Deteniendo multicast");
                        this.bQueue.add(true);
                        System.out.println("Escucha a ChromeCast pausada por tipeo de comando");
                        sleep(1000);
                        System.out.print("\r");
                        for(int i = 0; i < 40 ; i++){
                            System.out.print(" ");
                        }
                        System.out.print("\r>>Client"+this.clientID+": ");
                        fromUser = stdIn.readLine();
                        
                    } 
                    String command = "Client"+ String.valueOf( this.clientID ) + fromUser;
                    this.historyCommands.add(command);
                    
                    messageByte =  fromUser.getBytes();
                    
                    packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
                    kkSocket.send(packet);
                    
                    System.out.println("Se ha enviado petición: "+ fromUser + " al servidor");
                    this.bQueue.clear();
                }
            }
            System.out.println("Chao compare!");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName + " Intenta con otro puerto ");
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Problemas al intentar detener escuchas de ChromeCast para tipear comandos");
        }
    }
}