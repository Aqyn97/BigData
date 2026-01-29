import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {
    public static void main(String[] args) throws Exception {
        String[] nodes = {"http://localhost:9991","http://localhost:9992","http://localhost:9993"};
        String key = "topic";
        String value = "distributed systems";

        int success = 0;

        for (String node : nodes) {
            try {
                URL url = new URL(node + "/put");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);

                String json = "{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.close();

                if (conn.getResponseCode() == 200) success++;
            } catch (Exception e) {
                System.out.println("Node down: " + node);
            }
        }

        System.out.println("Write acknowledged by " + success + "/" + nodes.length);

        for (String node : nodes) {
            try {
                URL url = new URL(node + "/get?key=" + key);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = reader.readLine();
                    System.out.println("Read from " + node + ": " + response);
                    break;
                }
            } catch (Exception e) {
            
            }
        }
    }
}
