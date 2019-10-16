import java.net.*;
import java.io.*;

public class KnockKnockProtocol {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public String processInput(String theInput) {
        String theOutput = "";

        if (theInput == null){
            theOutput = "------------------------------\n" 
                    +   "------------------------------\n" 
                    +   "------------------------------\n" 
                    +   "---------W e l c o m e--------\n" 
                    +   "------------------------------\n" 
                    +   "------------------------------\n" 
                    +   "------------------------------\n";
            theOutput += this.getCommandsAvailable(); 
        }else{
            switch (theInput) {
                default:
                    theOutput += this.getCommandsAvailable();
                    break;
            }
        }

        return theOutput;
    }

    private String getCommandsAvailable(){
        String outString = "";
        outString = ANSI_YELLOW + "Comandos disponibles:" + ANSI_RESET + "\n";
        outString +=  "[0] \n"
                    + "[1] \n"
                    + "[2] \n"
                    + "[3] \n";
        return outString;
    }
}