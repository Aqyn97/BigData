import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class Node {
    private static HashMap<String, String> store = new HashMap<>();

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/put", exchange -> {
            if (!exchange.getRequestMethod().equals("PUT")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String body = reader.readLine();
            reader.close();

            String key = body.split("\"key\":\"")[1].split("\"")[0];
            String value = body.split("\"value\":\"")[1].split("\"")[0];

            store.put(key, value);

            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write("OK".getBytes());
            os.close();
        });

        server.createContext("/get", exchange -> {
            String query = exchange.getRequestURI().getQuery(); 
            String key = query.split("=")[1];

            String value = store.getOrDefault(key, "null");

            exchange.sendResponseHeaders(200, value.length());
            OutputStream os = exchange.getResponseBody();
            os.write(value.getBytes());
            os.close();
        });

        server.start();
        System.out.println("Node running on port " + port);
    }
}
