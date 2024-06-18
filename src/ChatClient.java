import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable{
    
    private String serverAddress;
    private InOutSocket inOutSocket;
    private Scanner scan;
    private String formatt = "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-";

    public ChatClient() {
        scan = new Scanner(System.in);
    }

    public void start() throws IOException{
        try {
            System.out.println("Digite o IP do servidor ao qual você quer acessar: ");
            serverAddress = scan.nextLine(); 
            inOutSocket = new InOutSocket(new Socket(serverAddress, Server.PORT));
            System.out.println("Cliente conectado ao servidor " + serverAddress + ":" + Server.PORT);

            System.out.print("Digite seu Username: ");
            String username = scan.nextLine();
            inOutSocket.sendMessage(username); // Envia para o servidor o Username do cliente para ser inserido na classe UserClient

            System.out.println(formatt);
            System.out.println("Padrão de mensagem -> @username@ mensagem");
            System.out.println(formatt);

            new Thread(this).start();
            messageLoop();

        } finally {
            inOutSocket.close();
        }
    }

    private void messageLoop() {
        String message;
        do {
            System.out.print("Digite sua mensagem >> ");
            message = scan.nextLine();

            // Garante a certa digitação da mensagem
            while(true) {
                String brokenMessage[] = message.split("@");

                // Paradas
                if((brokenMessage.length == 3) || (message.equalsIgnoreCase("!exit"))) break;

                System.out.println(formatt);
                System.err.println("Por favor, digite sua mensagem no padrão estabelecido \nPadrão de mensagem -> @username@ mensagem");
                System.out.println(formatt);
                System.out.print("Digite sua mensagem >> ");
                message = scan.nextLine();
            }

            inOutSocket.sendMessage(message);
            
        } while (!message.equalsIgnoreCase("!exit"));
    }

    // Recebe as mensagens direcionadas pelo servidor
    @Override
    public void run() {
        String message;
        while((message = inOutSocket.getMessage()) != null) {
            System.out.println();
            System.out.println(message);
        }
    }
       

    public static void main(String[] args) { 
        try {
            ChatClient client = new ChatClient();
            client.start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
