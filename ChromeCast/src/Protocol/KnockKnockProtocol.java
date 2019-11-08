package Protocol;

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

        //only first time --> protocol
        if (theInput == null){
            theOutput = "Presione c y ENTER para entrar a modo comandos a ChromeCast\n"
                        + "Presione q y ENTER para Salir\n";
            theOutput = " ------------------------------ \n" 
                    +   "/------------------------------\\\n" 
                    +   "|------------------------------|\n" 
                    +   "|---------"+ANSI_GREEN+"W e l c o m e"+ANSI_RESET+"--------|\n" 
                    +   "|------------------------------|\n" 
                    +   "|------------------------------|\n" 
                    +   " \\----------------------------/\n";
            theOutput += this.getCommandsAvailable(); 
        }else{
            switch (theInput) {
                case "0":
                    theOutput = "Hello ma' friend!";
                    break;
                case "1":
                    theOutput = "John Tell me that today is raining";
                    break;
                case "2":
                    theOutput = "HAHHAA!";
                    break;
                case "3":
                    theOutput = "¡Good Jaming!";
                    break;
                case "4":
                    theOutput = this.getCommandsAvailable();
                    break;
                case "6":
                    theOutput = "Bye ma' friend!";
                    break;
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
        outString +=  ANSI_PURPLE+"[0]"+ANSI_RESET+" Play  ----> uso: Play_Foyone - Presidente_120\n"
                    + ANSI_PURPLE+"[1]"+ANSI_RESET+" Stop  ----> uso: Stop\n"
                    + ANSI_PURPLE+"[2]"+ANSI_RESET+" Pause ----> uso: Pause\n"
                    + ANSI_PURPLE+"[3]"+ANSI_RESET+" Queue ----> uso: (1) Queue -> consulta la cola\n"
                    + ANSI_PURPLE+"   "+ANSI_RESET+"                  (2) Queue(<cancion>) -> añade canción a la cola\n"
                    + ANSI_PURPLE+"[4]"+ANSI_RESET+" Next  ----> uso: Next\n"
                    + ANSI_PURPLE+"[5]"+ANSI_RESET+" Jump  ----> uso: Jump\n"
                    + ANSI_PURPLE+"[6]"+ANSI_RESET+" Commands -> Mostrar historial de comandos\n"
                    + ANSI_PURPLE+"[7]"+ANSI_RESET+" Help: Mostrar historial de comandos\n"
                    + ANSI_PURPLE+"[8]"+ANSI_RESET+" Exit...\n";
        return outString;
    }
}