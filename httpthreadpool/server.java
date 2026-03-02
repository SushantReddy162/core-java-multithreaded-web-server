import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

    private final ExecutorService threadPool;

    public server(int poolSize){
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket){
        try (
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Read the HTTP headers until the blank line
            String line;
            while ((line = fromClient.readLine()) != null) {
                if (line.isEmpty()) {
                    break; 
                }
            }

            String htmlPage = "<html>" +
                              "<head><title>Java Web Server</title></head>" +
                              "<body style='font-family: Arial, sans-serif; text-align: center; margin-top: 50px;'>" +
                              "<h1>Welcome to my Custom Web Server!</h1>" +
                              "<p>Optimized for high-throughput Blocking I/O.</p>" +
                              "</body>" +
                              "</html>";

            // Send standard HTTP response
            toClient.print("HTTP/1.1 200 OK\r\n");
            toClient.print("Content-Type: text/html\r\n");
            toClient.print("Content-Length: " + htmlPage.length() + "\r\n");
            
            // Explicitly tell the browser we are hanging up immediately
            toClient.print("Connection: close\r\n"); 
            
            toClient.print("\r\n"); 
            toClient.print(htmlPage);
            toClient.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // The socket closes instantly, freeing the thread for the next user
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        int poolSize = 1000; 

        server server = new server(poolSize);
        
        try {
            ServerSocket socket = new ServerSocket(port, 10000);
            System.out.println("Web Server is listening on port " + port + " with " + poolSize + " threads.");

            while (true) { 
                Socket acceptedConnection = socket.accept();
                server.threadPool.execute(() -> server.handleClient(acceptedConnection));
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            server.threadPool.shutdown();
        }
    }
}