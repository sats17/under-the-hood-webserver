package thread_per_request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RawServer {

    private static void handleClient(Socket clientSocket) {
        System.out.println( "Received request on thread "+Thread.currentThread().threadId());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public static void main(String[] args) throws IOException {
        int port = 8082;
        ServerSocket serverSocket = new ServerSocket(port, 20);
        System.out.println("Server started on port " + port);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

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
            executorService.execute(() -> handleClient(clientSocket));
        }
    }

}
