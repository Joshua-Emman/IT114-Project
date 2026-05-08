Threat Detection Chat Server

Project Description:
This project is a Java multi-client chat application with basic threat detection. Multiple users can connect to the server at the same time and send
messages to a global chatroom. The server checks each message against a blacklist file. If a user sends suspicious messages, the server gives that user's
IP address strikes. After 3 strikes, the IP address is banned from the chat.

Files:
src/Client.java - Runs the client program and lets the user send and receive messages.
src/Server.java - Runs the server, accepts client connections, stores chat history, tracks strikes, and manages banned IPs.
src/ClientHandler.java - Handles each connected client on its own thread.
blacklist.txt - Contains suspicious words that the server checks messages against.
README.txt - Explains how to compile, run, and test the project.
.gitignore - Prevents compiled .class files from being added to GitHub.

How to Compile:
Open a terminal in the project folder and run:

javac src/*.java

How to Start the Server:
Run:

java -cp src Server

The server will start listening on port 6767.

How to Start a Client:
Open a new terminal and run:

java -cp src Client

To test multiple clients, open multiple terminals and run:

java -cp src Client

How to Use:
1. Start the server first.
2. Start one or more clients.
3. Each client will be asked to enter a username.
4. Type messages and press Enter to send them.
5. Type /quit to leave the chat.

Main Features:
- Multiple clients can connect at the same time.
- Each client is handled on its own thread.
- Users can send messages to a shared global chatroom.
- Chat history is stored while the server is running.
- New clients can see previous chat messages when they join.
- Messages are checked against blacklist.txt.
- Suspicious messages trigger a global warning.
- Each suspicious message gives the user's IP address 1 strike.
- After 3 strikes, the user's IP address is banned.
- Banned IP addresses cannot reconnect while the server is running.
- Blank usernames are handled by assigning the name Guest.
- Blank messages are rejected.
- The server handles client disconnects without crashing.

Core Requirements:
Network Communication:
The project uses Java TCP sockets with ServerSocket, Socket, BufferedReader, and PrintWriter. Clients and the server can send messages back and forth.

Concurrency:
The server creates a new ClientHandler thread for each connected client. This allows multiple clients to use the chat at the same time.

Shared State Management:
The server stores connected clients, chat history, strikes, and banned IP addresses using synchronized collections. This helps prevent problems when multiple
client threads access shared data.

Resilience and Error Handling:
The project catches IOExceptions, handles /quit, prevents blank messages, handles blank usernames, prevents sending messages before a client is ready, and
removes disconnected clients from the server list.

Testing:
To test the project:
1. Start the server.
2. Start two or more clients.
3. Send normal messages between clients.
4. Open a new client and confirm previous chat history appears.
5. Send a blacklisted word such as malware, virus, or phishing.
6. Confirm a global warning appears.
7. Send 3 blacklisted messages from the same client.
8. Confirm the client is banned.
9. Try reconnecting and confirm the banned IP is blocked.
10. Test /quit and blank messages.

Notes:
Chat history, strikes, and banned IP addresses are stored in memory while the server is running. If the server is restarted, this information resets.

When testing locally, all clients may use the same localhost IP address. This means banning one local client may block all local clients until the server
restarts. On a real network, each client device would have its own IP address.