package ClientProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String ANSI_BOLD = "\u001B[1m";


    //mensaje de bienvenida
    public int HelloChromeCast(byte[] messageByte) throws IOException {
        
        messageByte =  "HelloChromeCast".getBytes();
        
        packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,5007);
        kkSocket.send(packet); 
        
        //esperar id de cliente de servidor
        kkSocket.receive(packet);
        
        String received = new String(packet.getData(),0,packet.getLength());
        
        int clientID = Integer.parseInt(received);
        System.out.println("Bienvenido a ChromeCast cliente: " + clientID);
        System.out.println( welcomeMessage() );

        return clientID;
    }
    
    public String extractBruteCommand(String fromUser){
        String brute = "";
        String regex = "Client([0-9])+(->){1}'([\\w|\\s|_|-]+)'(\\s-\\s)ID:([0-9])+$";
        fromUser.matches(regex);
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fromUser);
        if (matcher.matches()) {
            brute = matcher.group(3);
        //     System.out.println("brute command " + brute);
        }

        return brute.toLowerCase();
    }

    public void processInput(String fromUser, byte[] messageByte) throws IOException {
        //extraer comando bruto

        String toLow = extractBruteCommand( fromUser );

        messageByte =  fromUser.getBytes();
        
        packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,5007);
        if( isOnlySend(toLow) ){
            if(isBadFormated(toLow)){
                System.out.println("Comando mal formateado");                
            } else{
                kkSocket.send(packet);
            }
        } else if(toLow.compareTo("queue") == 0 || toLow.compareTo("history") == 0){

            kkSocket.send(packet);
            
            messageByte = new byte[1000];
            
            packet = new DatagramPacket(messageByte, messageByte.length,groupAddress,5007);
            
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

    /**
     * verifica si el comando es solo de envío
     * ejemplo: comando queue necesita recepción dedicada del cliente
     * @param word
     * @return
     */
    private Boolean isOnlySend(String word){
        Boolean flag = false;
        if(word.contains("pause") || word.contains("next") || word.contains("jump")){
            flag = true;
        } else if(word.contains("play") || word.contains("stop") || word.contains("queue_")){
            flag = true;
        } 

        return flag;
    }

    /**
     * verifica si el comando esta mal formateado o no
     * @param word string a verificar formato
     * @return si está mal formateada : true
     */
    private Boolean isBadFormated(String word){
        String playRegex = "play_[\\w ]+-\\w+_\\d+$";
        String jumpRegex = "jump_\\d+$";
        String queueRegex = "queue_[\\w ]+-\\w+_\\d+$";
        Boolean flag = true; 

        if(word.contains("play") && word.matches(playRegex)){
            flag = false;
        } else if(word.contains("jump") && word.matches(jumpRegex)){
            flag = false;
            if(Integer.valueOf(word.split("_")[1]) <2){
                System.out.println("Comando jump funciona desde la posición 2");
                flag = true;
            }
        }else if(word.contains("queue_") && word.matches(queueRegex)){ 
            flag = false;
        }
        if(word.contains("pause") || word.contains("next") || word.contains("stop") ) flag = false;

        return flag;
    }

    public String welcomeMessage() {
        String theOutput = "";

        //only first time --> protocol
        theOutput = "";
        try{
            theOutput += this.getBanner();
        } catch(Exception e){
            System.out.println(e);
        }
        theOutput += this.getCommandsAvailable(); 

        return theOutput;
    }

    /**
     * Ayuda de comandos y como usarlos
     * @return
     */
    private String getCommandsAvailable(){
        String outString = "";
        outString = ANSI_YELLOW + ANSI_BOLD + "Comandos disponibles:" + ANSI_RESET + "\n";
        outString += ANSI_BLUE + ANSI_BOLD + "Información:"+ANSI_RESET+" <canción> = <autor>-<nombre>_<duración>" + ANSI_RESET + "\n";
        outString +=  ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Play_<canción> : Reproducir ahora\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Stop           : Limpiar cola y detener\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Pause          : Pausar o reanudar reproducción\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Queue          : Consulta la cola\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Queue_<canción>: añade canción a la cola\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Next           : Reproducir próxima canción de la cola\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Jump_<posición(desde 2)>: Saltar a posición en la cola\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" History       : Mostrar historial de comandos\n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Help           : Mostrar esta ayuda \n"
                    + ANSI_BOLD + ANSI_PURPLE+"[-]"+ANSI_RESET+" Exit...\n";
        outString += "Presione c+ENTER para entrar a modo comandos a ChromeCast\n"
                        + "Escriba exit+ENTER para salir\n";
        return outString;
    }

    /**
     * A quien no le gusta un banner?
     * @return
     * @throws Exception
     */
    private String getBanner() throws Exception {
        String outString = "";
        File file = new File("resources/banner");
        
        BufferedReader br = new BufferedReader( new FileReader(file) ); 
        
        String st; 
        while ((st = br.readLine()) != null) {
            outString += st + "\n"; 
        } 
        
        br.close();
        
        return outString;
    }
    
}