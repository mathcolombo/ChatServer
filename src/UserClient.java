public class UserClient {
    
    public InOutSocket ipUser; // Socket recebido na criação da conexão
    public String name;

    public UserClient(InOutSocket ipUser, String name) {
        this.ipUser = ipUser;
        this.name = name;
    }

    public void setIpUser(InOutSocket ipUser) {
        this.ipUser = ipUser;
    }

    public InOutSocket getIpUser() {
        return this.ipUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

