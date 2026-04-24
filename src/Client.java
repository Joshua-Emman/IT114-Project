import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 6767;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected to the server.");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            // lets the client receive messages while also typing
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;

                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }

                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            // Start the receiving thread
            readThread.start();

            // Main loop for typing and sending messages
            while (true) {
                String message = scanner.nextLine();

                out.println(message);

                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

            socket.close();

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}