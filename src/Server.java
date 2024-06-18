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

            UserClient userClient = new UserClient(inOutSocket, inOutSocket.getMessage());
            connectedClients.add(userClient);

            new Thread (() -> clientMessageLoop(userClient)).start(); // Thread que cuida das mensagens recebidas
        }
    }

    // Cuida da mensagem quando recebida
    private void clientMessageLoop(UserClient userClient) {
        String message;
        try {

            // Garante que há algo sendo enviado
            while((message = ((userClient.getIpUser()).getMessage())) != null) {
                // Comando de saída do chat
                if("!exit".equalsIgnoreCase(message)) {
                    connectedClients.remove(userClient);
                    disconnectionAlert(userClient, "@" + userClient.getName() + "@" + " se desconectou");
                    System.out.println();
                    System.out.println("CLIENTE " + userClient.getIpUser().getRemoteSocketAddressToString() + " SE DESCONECTOU:"); // mostra no servidor
                    return;
                }
                sendMessageToOneClient(userClient, message);
            }

        } finally {
            (userClient.getIpUser()).close();
        }
    }

    // Envia a mensagem para um client específico
    private void sendMessageToOneClient(UserClient sender, String message) {
        String brokenMessage[] = message.split("@");
        String receiver = brokenMessage[1];
        message = brokenMessage[2];

        synchronized (connectedClients) {
            for (UserClient userClient : connectedClients) {

                if(receiver.equalsIgnoreCase(userClient.getName())) {
                    (userClient.getIpUser()).sendMessage((">> @" + sender.getName()) + "@" + message);
                }
            }
        }
    }

    // Envia mensagem para todos os clientes quando alguém desconecta
    private void disconnectionAlert(UserClient emissor, String msg) {
        synchronized (connectedClients) {

            for (UserClient userClient : connectedClients) {
            userClient.getIpUser().sendMessage(msg); 
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
