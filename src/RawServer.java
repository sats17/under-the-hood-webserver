import java.io.*;
import java.net.*;

public class RawServer {
    public static void main(String[] args) throws IOException {
        int port = 8082;
        ServerSocket serverSocket = new ServerSocket(port, 1);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept(); // blocks until a client connects
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
            handleClient(clientSocket); // sequential for now
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              // hold the connection for 5 sec
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

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
