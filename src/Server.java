import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    public static final int PORT = 8080;
    private ServerSocket serverSocket;
    private final List<UserClient> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public void start() throws IOException{
        String LOCAL_IP = InetAddress.getLocalHost().getHostAddress(); // Armazena o IP local
        
        System.out.println("Servidor iniciado na porta " + PORT + " / IP: " + LOCAL_IP);


        serverSocket = new ServerSocket(PORT);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException{
        while(true) {
            InOutSocket inOutSocket = new InOutSocket(serverSocket.accept());

            String username = inOutSocket.getMessage();
            UserClient userClient = new UserClient(inOutSocket, username);
            connectedClients.add(userClient);

            new Thread (() -> clientMessageLoop(userClient)).start();
        }
    }

    private void clientMessageLoop(UserClient userClient) {
        String message;
        try {
            while((message = ((userClient.getIpUser()).getMessage())) != null) {
                if("exit".equalsIgnoreCase(message)) return;
                sendMessageToOneClient(userClient, message);
            }

        } finally {
            (userClient.getIpUser()).close();
        }
    }

    private void sendMessageToOneClient(UserClient sender, String message) {
        String brokenMessage[] = message.split(";");
        String receiver = brokenMessage[0];
        message = brokenMessage[1];

        synchronized (connectedClients) {
            for (UserClient userClient : connectedClients) {

                if(receiver.equalsIgnoreCase(userClient.getName())) {
                    (userClient.getIpUser()).sendMessage((sender.getName()) + ": " + message);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();

        } catch (IOException e) {
            System.out.println("Erro ao inicializar o Servidor / " + e.getMessage());
        }
    }
}
