import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private int maxSize;

    public ServerThread(Socket clientSocket, int maxSize) {
        this.clientSocket = clientSocket;
        this.maxSize = maxSize;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            String fileName = in.readUTF();
            long fileSize = in.readLong();

            if (fileSize <= maxSize) {
                out.writeUTF("OK"); // Inform the client that the file transfer can proceed

                FileOutputStream fileOut = new FileOutputStream(fileName);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }
                fileOut.close();
                System.out.println("File received: " + fileName);
            } else {
                out.writeUTF("ERROR: File size exceeds the limit");
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
