import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    // Socket for this specific client connection
    private Socket socket;

    private BufferedReader in;

    private PrintWriter out;

    private String username;

    private String clientIP;

    public ClientHandler(Socket socket, String clientIP) {
        this.socket = socket;
        this.clientIP = clientIP;
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
                message = message.trim();

                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (message.isEmpty()) {
                    out.println("Message cannot be blank.");
                    continue;
                }

                if (Server.containsBlacklistedWord(message)) {
                    int strikeCount = Server.addStrike(clientIP);

                    Server.broadcast("[GLOBAL WARNING] Suspicious message detected from " + username + " (" + strikeCount + "/3 strikes)");

                    out.println("Your message was flagged and was not sent.");

                    if (strikeCount >= 3) {
                        out.println("You have been banned for sending too many suspicious messages.");
                        break;
                    }

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