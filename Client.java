import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Server's IP address or hostname
        int serverPort = 12345; // Server's port
        String fileName = "example.txt"; // Change this to the file you want to send
        long fileSize = new File(fileName).length();

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            out.writeUTF(fileName);
            out.writeLong(fileSize);

            String response = in.readUTF();
            if (response.equals("OK")) {
                FileInputStream fileIn = new FileInputStream(fileName);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                fileIn.close();
                System.out.println("File sent: " + fileName);
            } else {
                System.out.println("Server rejected file transfer: " + response);
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
