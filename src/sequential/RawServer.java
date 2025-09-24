package sequential;

import java.io.*;
import java.net.*;

public class RawServer {
    public static void main(String[] args) throws IOException {
        int port = 8082;
        ServerSocket serverSocket = new ServerSocket(port, 1);
        System.out.println("Server started on port " + port);

        while (true) {
            // This line blocks until a new client finishes TCP handshake
            // Under the hood:
            //   - serverSocket is backed by an OS-level "listening socket"
            //   - accept() calls the POSIX socket API -> accept(fd)
            //   - if no connections are ready in the kernel's backlog queue, it waits (blocks)
            //   - once a client has completed 3-way TCP handshake, the kernel dequeues it
            //     and returns a new socket dedicated to this client
            Socket clientSocket = serverSocket.accept();

            System.out.println("TCP Handshake is done, Accepted connection from " + clientSocket.getRemoteSocketAddress());

            // Handle the client in this same thread (sequentially).
            // Important: while handleClient() runs, this loop does not call accept() again.
            // That means new incoming connections sit in the OS backlog until this finishes.
            handleClient(clientSocket); // sequential for each request connection.
        }
    }

    private static void handleClient(Socket clientSocket) {
        // Uncomment this and make backlog size to 1 to understand backlog concept.
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("Request before reader");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

            System.out.println("Printing before receiving any data");
            // Read HTTP request line (e.g., GET / HTTP/1.1)
            String line = in.readLine();
            System.out.println("Request: " + line);

            // Write HTTP response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("Hello from SimpleHttpServer!");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
