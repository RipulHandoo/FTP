import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class Server {
     private static final String SERVER_DIRECTORY = "C:\\Users\\HP\\Desktop\\CN_Project\\received_files"; // Directory containing server files
 
    public static void main(String[] args) {
        int port = 12345; // Set the server port

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                // Create a new thread to handle the client request
                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                // Read the client's request
                String request = in.readUTF();

                if (request.equals("PUT")) {
                    // Handle file upload
                    receiveFile(in, out);
                } else if (request.equals("GET")) {
                    // Handle file download request
                    String fileName = in.readUTF();
                   sendFile(fileName, out);
                } else if (request.equals("LIST")) {
                    // Handle file list request
                    listFiles(out);
                } else {
                    System.out.println("Unknown command: " + request);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }   
    }
    private static void receiveFile(DataInputStream in, DataOutputStream out) throws IOException {
            // Read the file name from the client
            try{
                String fileName = in.readUTF();
                System.out.println("Receiving file: " + fileName);

                // Define a directory for saving received files
                String saveDirectory = "received_files";
                File saveDir = new File(saveDirectory);
                if (!saveDir.exists()) {
                    saveDir.mkdir();
                }

                // Add a timestamp to the saved file's name
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
                String savedFileName = saveDirectory + File.separator + timestamp + "_" + fileName;

                // Read the file data and write it to the server
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                FileOutputStream fileOutputStream = new FileOutputStream(savedFileName);

                while ((bytesRead = in.read(buffer, 0, bufferSize)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                fileOutputStream.close();
                System.out.println("File received and saved as: " + savedFileName);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private static  void sendFile(String fileName, DataOutputStream out) throws IOException {
            File file = new File(SERVER_DIRECTORY, fileName);

            if (file.exists() && file.isFile()) {
                out.writeUTF("File found");
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                out.writeUTF("File not found");
            }
        }

        private static void listFiles(DataOutputStream out) throws IOException {
            File directory = new File(SERVER_DIRECTORY);
            File[] files = directory.listFiles();

            if (files != null) {
                String[] fileNames = Arrays.stream(files)
                        .filter(File::isFile)
                        .map(File::getName)
                        .toArray(String[]::new);

                out.writeInt(fileNames.length);
                for (String fileName : fileNames) {
                    out.writeUTF(fileName);
                }
            } else {
                out.writeInt(0);
            }
        }
    }  