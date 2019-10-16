
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
            String fromServer;
            String fromUser;

            byte[] messageByte = new byte[1000];
            boolean end = false;
            String dataString = "";

            while (in.available()>0) {
                //No muy util ya que no se limpia el buffer
                int bytesRead = in.read(messageByte);
                dataString += new String(messageByte, 0, bytesRead);
                
                System.out.println(dataString);
                // System.out.println(fromServer);
                dataString = "";
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    // enviamos al server peticion del usuario
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}