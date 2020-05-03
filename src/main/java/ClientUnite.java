import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientUnite {
    private static Logger LOG = LogManager.getLogger(ClientUnite.class);
    private Socket socket;
    private List<String> imageList = new ArrayList<>();
    private InetAddress address;
    private int port;

    public ClientUnite(InetAddress address, int port) {
        try {
            LOG.info("Creating the socket\n");
            socket = new Socket(address, port);
            this.address = address;
            this.port = port;
        } catch (IOException e) {
            LOG.error("Input/Output exception: " + e + "\n");
        }
    }

    public void getRequest() {
        try {
            LOG.info("Making the GET request\n");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET / HTTP/1.1");
            createRequest(out);
            out.flush();
            findImages();
            out.close();
            socket.close();
        } catch (IOException e) {
            LOG.error("Input/Output exception: " + e + "\n");
        }
    }

    private void findImages() {
        LOG.info("Finding the images\n");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                    StandardCharsets.US_ASCII));
            String line;
            String pattern = "/images/[^ ]*(.jpg|.gif|.png)+";
            while ((line = in.readLine()) != null) {
                Matcher matcher = Pattern.compile(pattern).matcher(line);
                if (matcher.find()) {
                    imageList.add(matcher.group());
                    LOG.info("Found image: [" + matcher.group() + "]\n");
                }
            }
            in.close();
        } catch (IOException e) {
            LOG.error("Input/Output exception: " + e + "\n");
        }
    }

    private void createRequest(PrintWriter out) {
        out.println("Host: " + socket.getInetAddress().getHostName());
        out.println("Cache-Control: no-cache");
        out.println("Server: Apache");
        out.println("Vary: Accept-Encoding,Cookie");
        out.println("Cache-Control: max-age=3, must-revalidate");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("");
    }

    public void getImages() {
        LOG.info("Getting images\n");
        try {
            for (String image : imageList) {
                socket = new Socket(address, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("GET " + image + " HTTP/1.1");
                createRequest(out);
                CustomThread thread = new CustomThread(socket, image);
                thread.start();
            }
        } catch (IOException e) {
            LOG.error("Input/Output exception: " + e + "\n");
        }
    }
}
