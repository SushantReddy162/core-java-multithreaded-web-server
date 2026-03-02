
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server{
    private final ExecutorService threadPool;
    public server(int poolSize){
         this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket){
        try {
            PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(),true);
            toSocket.println("hello from the server"+ clientSocket.getInetAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        int port = 8080;
        int threadPool = 100;

        server server = new server(threadPool);

        try {
            ServerSocket socket = new ServerSocket(port,10000);
            //socket.setSoTimeout(70000);
            System.out.println("server is listening on port"+ port);
            
            while (true) { 
                Socket acceptedConnection = socket.accept();
                server.threadPool.execute(()->server.handleClient(acceptedConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            server.threadPool.shutdown();
        }
    }
}