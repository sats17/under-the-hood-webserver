import java.io.*;
import java.net.Socket;


public class RawClient {
    public static void main(String[] args) {
        int port = 8082;

        for (int i = 0; i < 10; i++) {
            final int id = i;
            new Thread(() -> {
                try (Socket socket = new Socket("localhost", port)) {
                    System.out.println("Client " + id + " connected!");

                    // Send a minimal HTTP request
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("GET / HTTP/1.1");
                    out.println("Host: localhost");
                    out.println(); // blank line ends headers
                    out.flush();

                    // Read response
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    String line;
                    System.out.println("Client " + id + " response:");
                    while ((line = in.readLine()) != null) {
                        System.out.println("  " + line);
                    }

                } catch (Exception e) {
                    System.out.println("Client " + id + " failed: " + e.getMessage());
                }
            }).start();
        }
    }
}
