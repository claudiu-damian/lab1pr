import java.io.IOException;
import java.net.InetAddress;

public class RunClient {

    public static void main(String[] args) throws IOException {
        InetAddress addressUnite = InetAddress.getByName("unite.md");
        ClientUnite uniteClient = new ClientUnite(addressUnite, 80);
        uniteClient.getRequest();
        uniteClient.getImages();
    }
}
