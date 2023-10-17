import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345; // Specify the port to listen on
        int maxSize = 10 * 1024 * 1024; // 10MB maximum file size

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("FTP Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ServerThread(clientSocket, maxSize).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
