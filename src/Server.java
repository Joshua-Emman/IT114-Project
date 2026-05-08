import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 6767;

    // Shared list of all connected clients and synchronizedList helps prevent issues when multiple threads access it
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    // stores the blacklisted words
    private static final List<String> blacklist = new ArrayList<>();

    // stores all chat messages so new users can see previous messages
    private static final List<String> chatHistory = Collections.synchronizedList(new ArrayList<>());

    // keeps track of how many suspicious messages each IP has sent
    private static final Map<String, Integer> strikes = Collections.synchronizedMap(new HashMap<>());

    // stores IPs that are banned after 3 strikes
    private static final Set<String> bannedIPs = Collections.synchronizedSet(new HashSet<>());

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

                String clientIP = clientSocket.getInetAddress().getHostAddress();

                if (isBanned(clientIP)) {
                    PrintWriter tempOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    tempOut.println("You are banned from this chat.");
                    clientSocket.close();
                    System.out.println("Blocked banned IP: " + clientIP);
                    continue;
                }

                System.out.println("A client connected: " + clientSocket.getInetAddress());

                // create a handler for this client
                ClientHandler handler = new ClientHandler(clientSocket, clientIP);

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
        addToHistory(message);

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public static void addToHistory(String message) {
        chatHistory.add(message);
    }

    public static List<String> getChatHistory() {
        synchronized (chatHistory) {
            return new ArrayList<>(chatHistory);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static int addStrike(String ip) {
        int currentStrikes = strikes.getOrDefault(ip, 0) + 1;

        strikes.put(ip, currentStrikes);

        if (currentStrikes >= 3) {
            bannedIPs.add(ip);
            System.out.println("IP banned: " + ip);
        }

        return currentStrikes;
    }

    public static boolean isBanned(String ip) {
        return bannedIPs.contains(ip);
    }

}