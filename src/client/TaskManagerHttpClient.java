package client;

import java.net.http.HttpClient;

public class TaskManagerHttpClient {

    private static HttpClient client = HttpClient.newBuilder()
            .build();

    public static HttpClient getClient() {
        return client;
    }
}
