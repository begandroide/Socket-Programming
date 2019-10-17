
import java.io.*;
import java.net.*;

public class KnockKnockClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java KnockKnockClient <host_name> <port_number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            // socket del cliente, conectado al server en el puerto arg
            Socket kkSocket = new Socket(hostName, portNumber);

            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            
            DataInputStream in = new DataInputStream(kkSocket.getInputStream());
            )
            {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;
            String fromServer = "";

            byte[] messageByte = new byte[1000];

            Boolean brokePipe = false;
            while(!brokePipe){

                    while (in.available() > 0) {
                        System.out.println("Nuevo mensaje disponible");
                        int bytesRead = in.read(messageByte);
                        fromServer += new String(messageByte, 0, bytesRead);
                        System.out.println(fromServer);
                        fromServer = "";
                        
                        fromUser = stdIn.readLine();
                        if(fromUser.equals( "5")){
                                brokePipe = true;
                                break;
                        } 
                        if (fromUser != null) {
                                System.out.println("Se ha enviado petición: "+ fromUser + " al servidor");
                                // enviamos al server peticion del usuario
                                out.println(fromUser);
                                out.flush();
                        }
                    }
            }
            System.out.println("Chao compare!");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}