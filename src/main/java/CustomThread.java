import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class CustomThread extends Thread {
    private Logger LOG = LogManager.getLogger(CustomThread.class);
    private Socket socket;
    private String name;

    CustomThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        String imagePathName = "";
        if (name.contains("/images"))
            imagePathName = name.replace("/images", "");
        File file = new File(Constants.FOLDER_PATH + imagePathName);
        LOG.info("Downloading image: [" + name + "] to: [" + file.getPath() + "]\n");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = socket.getInputStream();
            boolean headerEnded = false;
            byte[] bytes = new byte[2048];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                if (headerEnded) {
                    fileOutputStream.write(bytes, 0, length);
                } else {
                    for (int i = 0; i < 2048; i++) {
                        if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                            headerEnded = true;
                            fileOutputStream.write(bytes, i + 4, 2048 - i - 4);
                            break;
                        }
                    }
                }
            }
            inputStream.close();
            fileOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
