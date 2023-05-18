package server;

import service.exceptions.ResponseException;
import service.exceptions.StatusCodeException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken = null;
    private final String port;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String SERVER_NAME = "http://localhost:";

    public KVTaskClient(String port) {
        this.port = port;
        URI uri = URI.create(SERVER_NAME + port + "/register");
        HttpRequest request = HttpRequest.newBuilder().
                uri(uri).
                GET().
                build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new StatusCodeException("Статус ответа не 200");
            } else {
                apiToken = response.body();
            }
        } catch (IOException | InterruptedException e) {
            throw new ResponseException("Во время выполнения запроса ресурса по url-адресу: '" + uri + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(SERVER_NAME + port + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(uri).
                POST(body).
                build();
        try {
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new StatusCodeException("Статус ответа не 200");
            }
        } catch (IOException | InterruptedException e) {
            throw new ResponseException("Во время выполнения запроса ресурса по url-адресу: '" + uri + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        URI uri = URI.create(SERVER_NAME + port + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.
                newBuilder().
                uri(uri).
                GET().
                build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ResponseException("Во время выполнения запроса ресурса по url-адресу: '" + uri + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return response.body();
    }
}