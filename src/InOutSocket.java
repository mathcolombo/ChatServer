import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class InOutSocket {
    
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public InOutSocket(Socket socket) throws IOException{
        this.socket = socket;

        System.out.println();
        System.out.println("CLIENTE " + socket.getRemoteSocketAddress() + " CONECTOU:");

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Tratamento de uma Stream para conseguir receber uma String
        this.out = new PrintWriter(socket.getOutputStream(), true); // Tratamento de uma Stream para conseguir passar uma String e não precisar ficar quebrando linha
    }

    public String getMessage() {
        try {
            return in.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean sendMessage(String message) {
        out.println(message);
        return !out.checkError();
    }

    public String getRemoteSocketAddressToString() {
        return "" + socket.getRemoteSocketAddress();
    } 

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
            
        } catch (Exception e) {
            System.out.println("Erro ao fechar o socket");
        }
    }
}
