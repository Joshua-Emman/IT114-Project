import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    // Socket for this specific client connection
    private Socket socket;

    private BufferedReader in;

    private PrintWriter out;

    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // set up input stream to receive data from client
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter username:");

            username = in.readLine();

            out.println("----- Chat History -----");

            for (String oldMessage : Server.getChatHistory()) {
                out.println(oldMessage);
            }

            out.println("------------------------");

            Server.broadcast(username + " joined the chat.");

            String message;

            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (Server.containsBlacklistedWord(message)) {
                    Server.broadcast("[GLOBAL WARNING] Suspicious message detected from " + username);
                } else {
                    Server.broadcast(username + ": " + message);
                }
            }

        } catch (IOException e) {
            // Handles unexpected client disconnects
            System.out.println("Client disconnected.");
        } finally {
            // Remove the client no matter how they disconnected
            Server.removeClient(this);

            if (username != null) {
                Server.broadcast(username + " left the chat.");
            }

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket.");
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}