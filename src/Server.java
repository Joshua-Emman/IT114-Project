import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 6767;

    // Shared list of all connected clients and synchronizedList helps prevent issues when multiple threads access it
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    // stores the blacklisted words
    private static final List<String> blacklist = new ArrayList<>();

    public static void main(String[] args) {
        // Load the suspicious words before starting the server
        loadBlacklist();

        System.out.println("Starting server...");

        // create the serversocket and listen for clients
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Keep accepting new clients forever
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A client connected: " + clientSocket.getInetAddress());

                // create a handler for this client
                ClientHandler handler = new ClientHandler(clientSocket);

                clients.add(handler);

                // Start a new thread for this client
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            // handles server startup or connection errors
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Reads suspicious words from blacklist.txt
    private static void loadBlacklist() {
        try (BufferedReader reader = new BufferedReader(new FileReader("blacklist.txt"))) {
            String word;

            while ((word = reader.readLine()) != null) {
                if (!word.trim().isEmpty()) {
                    blacklist.add(word.trim().toLowerCase());
                }
            }

            System.out.println("Blacklist loaded: " + blacklist);

        } catch (IOException e) {
            // server still runs if file is missing
            System.out.println("Could not load blacklist.txt");
        }
    }

    public static boolean containsBlacklistedWord(String message) {
        String lowerMessage = message.toLowerCase();

        for (String word : blacklist) {
            if (lowerMessage.contains(word)) {
                return true;
            }
        }

        return false;
    }

    public static void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}