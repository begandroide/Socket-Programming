package ClientProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class KnockKnockProtocol {

    private InetAddress groupAddress;
    private String hostName;
    private DatagramPacket packet;
    private DatagramSocket kkSocket;

    public KnockKnockProtocol(String hostName, DatagramPacket packet, DatagramSocket kSocket)
            throws UnknownHostException {
        this.hostName = hostName;
        this.packet = packet;
        this.kkSocket = kSocket;
        this.groupAddress = InetAddress.getByName(this.hostName);
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    //mensaje de bienvenida
    public int HelloChromeCast(byte[] messageByte) throws IOException {
        
        messageByte =  "HelloChromeCast".getBytes();
        
        packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
        kkSocket.send(packet); 
        
        //esperar id de cliente de servidor
        kkSocket.receive(packet);
        
        String received = new String(packet.getData(),0,packet.getLength());
        
        int clientID = Integer.parseInt(received);
        System.out.println("Bienvenido a ChromeCast cliente: " + clientID);
        System.out.println( welcomeMessage() );

        return clientID;
    }
    

    public void processInput(String fromUser, byte[] messageByte) throws IOException {
        String toLow = fromUser.toLowerCase();

        messageByte =  fromUser.getBytes();
        
        packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
        if( isOnlySend(toLow) ){

            kkSocket.send(packet);
        } else if(toLow.compareTo("queue") == 0 ){

            kkSocket.send(packet);
            
            messageByte = new byte[1000];
            
            packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,4447);
            
            kkSocket.receive(packet);
    
            String received = new String(packet.getData(),0,packet.getLength());
            System.out.println(received);        
        } else if(toLow.compareTo("help") == 0){
            System.out.println( this.getCommandsAvailable() );
        } else{
            //no se entienden
            System.out.println("Comando no conocido");
        }
    }

    private Boolean isOnlySend(String word){
        Boolean flag = false;
        if(word.contains("pause") || word.contains("next") || word.contains("jump")){
            flag = true;
        } else if(word.contains("play") || word.contains("stop") || word.contains("queue_")){
            flag = true;
        } 

        return flag;
    }

    public String welcomeMessage() {
        String theOutput = "";

        //only first time --> protocol
        theOutput = "Presione c y ENTER para entrar a modo comandos a ChromeCast\n"
                    + "Presione q y ENTER para Salir\n";
        theOutput += this.banner;
        theOutput += this.getCommandsAvailable(); 

        return theOutput;
    }

    private String getCommandsAvailable(){
        String outString = "";
        outString = ANSI_YELLOW + "Comandos disponibles:" + ANSI_RESET + "\n";
        outString +=  ANSI_PURPLE+"[0]"+ANSI_RESET+" Play  ----> uso: Play_Foyone - Presidente_120\n"
                    + ANSI_PURPLE+"[1]"+ANSI_RESET+" Stop  ----> uso: Stop\n"
                    + ANSI_PURPLE+"[2]"+ANSI_RESET+" Pause ----> uso: Pause\n"
                    + ANSI_PURPLE+"[3]"+ANSI_RESET+" Queue ----> uso: (1) Queue -> consulta la cola\n"
                    + ANSI_PURPLE+"   "+ANSI_RESET+"                  (2) Queue(<cancion>) -> añade canción a la cola\n"
                    + ANSI_PURPLE+"[4]"+ANSI_RESET+" Next  ----> uso: Next\n"
                    + ANSI_PURPLE+"[5]"+ANSI_RESET+" Jump  ----> uso: Jump_<posicion(desde 1: head=0)>\n"
                    + ANSI_PURPLE+"[6]"+ANSI_RESET+" Commands -> Mostrar historial de comandos\n"
                    + ANSI_PURPLE+"[7]"+ANSI_RESET+" Help: Mostrar historial de comandos\n"
                    + ANSI_PURPLE+"[8]"+ANSI_RESET+" Exit...\n";
        return outString;
    }


    private String banner =
    "────·────·───·────·────·────·────·────·─────·─────·────·────·────·────·────·\n"+
    "      _____   __    __   ___ _   ____   ____     ____  ______               \n"+
    "     /  ___| |  |  |  | |  |/ | /    \\  |   \\   /   | |  ____|             \n"+
    "    |  |     |  |__|  | |   _/ |  ──  | |    \\_/    | | |___               \n"+
    "    |  |___  |   __   | |  |   | |  | | |  |\\___/|  | | |____              \n"+
    "     \\     | |  |  |  | |  |   |  ──  | |  |     |  | |      |              \n"+
    "      ─────  ────  ────  ──     ──────  ────     ────  ──────               \n"+
    "               _____   _____   ______     ______________                    \n"+
    "              /  ___| |  _  | |  _____| |←↓→ ←↓→ ←↓→ ←↓|                    \n"+ 
    "             |  |     | |_| | \\  \\____   ─── ←↓→ ──────                   \n"+ 
    "             |  |___  | |─| |  ______//     |←↓→|                           \n"+ 
    "              \\     | | | | | /      /      |←↓→|                           \n"+ 
    "               ─────   ─   ─   ──────       ─────      ¢ŧłø                 \n"+
    "                       By @begandroide                                      \n"+ 
    "         Herramienta de simulación de un reproductor ChromeCast             \n"+ 
    "────·────·───·────·────·────·────·────·─────·─────·────·────·────·────·────·\n";

}