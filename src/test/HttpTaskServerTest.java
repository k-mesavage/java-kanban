package test;

import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private final Gson gson = Managers.getGson();
    private HttpClient client;



    @BeforeEach
    public void start() throws IOException, InterruptedException {
        this.kvServer = new KVServer();
        kvServer.start();
        this.httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
        TaskManager manager = httpTaskServer.getManager();
        Task task1 = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));
        manager.addTask(task1);
        manager.getTaskById(task1.getId());
        Task task2 = new Task("Task", "D", LocalDateTime.now().plusDays(1), Duration.ofHours(1));
        manager.addTask(task2);
        manager.getTaskById(task2.getId());
        Epic epic = new Epic("Epic", "D");
        manager.addEpic(epic);
        manager.getEpicById(epic.getId());
        SubTask subtask1 = new SubTask("Subtask",
                "D", epic.getId(), LocalDateTime.now().plusDays(2), Duration.ofHours(1));
        manager.addSubTask(subtask1);
        manager.getSubTaskById(subtask1.getId());
        SubTask subtask2 = new SubTask("Subtask",
                "D", epic.getId(), LocalDateTime.now().plusDays(3), Duration.ofHours(1));
        manager.addSubTask(subtask2);
        manager.getSubTaskById(subtask2.getId());
    }

    @AfterEach
    public void stop() {
        kvServer.stop();
        httpTaskServer.stop();
        httpTaskServer.getManager().cleanHistory();
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


    @Test
    public void addTask() throws IOException, InterruptedException {
        Task task3 = new Task("Task", "d", LocalDateTime.now().plusDays(4), Duration.ofHours(1));
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task3);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "D");
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String>response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void addSubTask() {
        Epic epic = new Epic("Epic", "D");
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            if (postResponse.statusCode() == 200) {
                SubTask subtask = new SubTask("SubTask", "D", epic.getId()
                        , LocalDateTime.now().plusDays(6), Duration.ofHours(1));
                url = URI.create("http://localhost:8080/tasks/subtask/");
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(1, arrayTasks.size());
            }
    } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Task", "D", LocalDateTime.now().plusDays(6), Duration.ofHours(1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            if (postResponse.statusCode() == 200) {
                url = URI.create("http://localhost:8080/tasks/task/" + "?id=" + task.getId());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("Epic", "D");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setId(id);
                url = URI.create("http://localhost:8080/tasks/epic/" + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);
                assertEquals(epic, responseTask);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtaskById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("Epic", "D");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            if (postResponse.statusCode() == 200) {
                SubTask subtask = new SubTask("description1", "name1", epic.getId()
                        , LocalDateTime.now().plusDays(7), Duration.ofHours(1));
                url = URI.create("http://localhost:8080/tasks/subtask/");
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create("http://localhost:8080/tasks/subtask/" + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    SubTask responseTask = gson.fromJson(response.body(), SubTask.class);
                    assertEquals(subtask, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTasks() {
        URI url = URI.create("http://localhost:8080/tasks/");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtasks() {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpics() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTaskById() {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Task", "D", LocalDateTime.now().plusDays(6), Duration.ofHours(1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            if (postResponse.statusCode() == 200) {
                url = URI.create("http://localhost:8080/tasks/task/" + "?id=" + task.getId());
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEpicById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("Epic", "D");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            if (postResponse.statusCode() == 200) {
                url = URI.create("http://localhost:8080/tasks/epic/" + "?id=" + epic.getId());
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteSubtaskById() {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("Epic", "D");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, postResponse.statusCode());
            SubTask subtask = new SubTask("SubTask", "D", epic.getId()
                    , LocalDateTime.now().plusDays(6), Duration.ofHours(1));
            url = URI.create("http://localhost:8080/tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void deleteAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
