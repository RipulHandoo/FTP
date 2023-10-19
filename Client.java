import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Set the server address
        int serverPort = 12345; // Set the server port

        try (Socket socket = new Socket(serverAddress, serverPort);
        // Receive the data from the server
             DataInputStream in = new DataInputStream(socket.getInputStream());
            //  Send the data to the server
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Send the FTP command to the server
                Scanner scanner = new Scanner(System.in);
             while (true) {
                // Display a menu of available commands
                System.out.println("Choose a command:");
                System.out.println("1. PUT (Upload a file)");
                System.out.println("2. GET (Download a file)");
                System.out.println("3. LIST (List server files)");

                // Prompt the user to enter a command choice
                System.out.print("Enter the number of the command (or 0 to exit): ");
                int choice = scanner.nextInt();

                if (choice == 0) {
                    // Exit the loop and terminate the client
                    break;
                }

                String command;
                switch (choice) {
                    case 1:
                        command = "PUT";
                        break;
                    case 2:
                        command = "GET";
                        break;
                    case 3:
                        command = "LIST";
                        break;
                    default:
                        System.out.println("Unknown command");
                        continue; // Continue the loop to re-enter a valid command
                }

                // Send the FTP command to the server
                out.writeUTF(command);

                if (command.equals("PUT")) {
                    // Send the file for upload
                    System.out.print("Enter the file name to upload: ");
                    String fileName = scanner.next();
                    // Send the entered file name to the server
                    out.writeUTF(fileName);
                    sendFileForUpload(in, out, fileName);
                } else if (command.equals("GET")) {
                    // Request a file download
                    System.out.print("Enter the file name to download: ");
                    String fileName = scanner.next();
                     out.writeUTF(fileName);
                    requestFileDownload(in, fileName);
                } else if (command.equals("LIST")) {
                    // Request a file list from the server
                    listServerFiles(in);
                } else {
                    System.out.println("Unknown command");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFileForUpload(DataInputStream in, DataOutputStream out, String fileName) throws IOException {
    // String fileName = "ky69jzczhjAI_82_Assigment2 (1).docx"; // Change this to the file you want to send

    try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
        File file = new File(fileName);

        // Send the file name to the server
        out.writeUTF(file.getName());

        // Send the file data to the server
        byte[] buffer = new byte[1024];
        int bytesRead;
        long totalBytesSent = 0;
        int packetNumber = 0;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            packetNumber++;
            out.write(buffer, 0, bytesRead);
            totalBytesSent += bytesRead;
            System.out.println("Packet " + packetNumber + " sent (" + bytesRead + " bytes)");
            
            // Update progress here if needed
            // Example: display totalBytesSent / file.length() as a percentage
        }

        System.out.println("File sent successfully.");
    } catch (IOException e) {
        e.printStackTrace();
        // Handle exceptions or send an error message to the client
    }
}

private static void requestFileDownload(DataInputStream in,String fileName) throws IOException {
    String saveDirectory = "downloaded_files"; // Directory where downloaded files will be saved
    // String fileName = "20231019103038_ky69jzczhjAI_82_Assigment2 (1).docx";

    // Create the directory if it doesn't exist
    File saveDir = new File(saveDirectory);
    if (!saveDir.exists()) {
        saveDir.mkdir();
    }

    String savedFileName = saveDirectory + File.separator + fileName;
    try (FileOutputStream fileOutputStream = new FileOutputStream(savedFileName)) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        int packetNumber = 0;

        while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1) {
            packetNumber++;
            fileOutputStream.write(buffer, 0, bytesRead);
            System.out.println("Packet " + packetNumber + " received (" + bytesRead + " bytes)");
        }

        System.out.println("File downloaded and saved as: " + savedFileName);
    } catch (IOException e) {
        e.printStackTrace();
        // Handle exceptions or display an error message
    }
}

private static void listServerFiles(DataInputStream in) throws IOException {
    int fileCount = in.readInt();
    System.out.println("List of server files:");
    for (int i = 0; i < fileCount; i++) {
        String fileName = in.readUTF();
        System.out.println(fileName);
    }
}

}
