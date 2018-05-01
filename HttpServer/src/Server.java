import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args)throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(999),0);
        server.createContext("/XO", new XO());
        server.start();
    }
}