package test;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


import model.Status;
import server.HttpTaskServer;
import server.KVServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.SubTask;
import model.Task;
import service.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new LocalDateTimeAdapter()).create();
    private static final String TASK_BASE_URL = "http://localhost:8080/tasks/task/";
    private static final String EPIC_BASE_URL = "http://localhost:8080/tasks/epic/";
    private static final String SUBTASK_BASE_URL = "http://localhost:8080/tasks/subtask/";

    @BeforeAll
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @AfterAll
    static void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }

    @BeforeEach
    void resetServer() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(EPIC_BASE_URL);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(SUBTASK_BASE_URL);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                SubTask subtask = new SubTask("SubTask", "D", epicId
                        , LocalDateTime.now().plusDays(2), Duration.ofHours(1));
                url = URI.create(SUBTASK_BASE_URL);

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
    void shouldGetTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setId(id);
                url = URI.create(TASK_BASE_URL + "?id=" + id);
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
    void shouldGetEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setId(id);
                url = URI.create(EPIC_BASE_URL + "?id=" + id);
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
    void shouldGetSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                SubTask subtask1 = new SubTask("SubTask", "D", epicId
                        , LocalDateTime.now().plusDays(2), Duration.ofHours(1));
                url = URI.create(SUBTASK_BASE_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask1.setId(id);
                    url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    SubTask responseTask = gson.fromJson(response.body(), SubTask.class);
                    assertEquals(subtask1, responseTask);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldUpdateTask() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                task.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(TASK_BASE_URL + "?id=" + id);
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
    void shouldUpdateEpic() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                epic.setStatus(Status.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create(EPIC_BASE_URL + "?id=" + id);
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
    void shouldUpdateSubtask() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                SubTask subtask = new SubTask("SubTask", "D", epicId
                        , LocalDateTime.now().plusDays(2), Duration.ofHours(1));
                url = URI.create(SUBTASK_BASE_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setStatus(Status.IN_PROGRESS);
                    request = HttpRequest.newBuilder()
                            .uri(url)
                            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                            .build();
                    client.send(request, HttpResponse.BodyHandlers.ofString());

                    url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
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
    void shouldDeleteTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body());
                epic.setId(epicId);
                SubTask subtask = new SubTask("SubTask", "D", epic.getId()
                        , LocalDateTime.now().plusDays(2), Duration.ofHours(1));
                url = URI.create(SUBTASK_BASE_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());
                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray arrayTasks = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, arrayTasks.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(TASK_BASE_URL);
        Task task = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body());
            url = URI.create(TASK_BASE_URL + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Задача с данным id не найдена", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body());
                url = URI.create(EPIC_BASE_URL + "?id=" + id);
                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(204, response.statusCode());

                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Эпик с данным id не найден", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldDeleteSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(EPIC_BASE_URL);
        Epic epic = new Epic("Epic", "D");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                SubTask subtask = new SubTask("SubTask", "D", epic.getId()
                        , LocalDateTime.now().plusDays(2), Duration.ofHours(1));
                url = URI.create(SUBTASK_BASE_URL);

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body());
                    subtask.setId(id);
                    url = URI.create(SUBTASK_BASE_URL + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).DELETE().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(204, response.statusCode());

                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Подзадача с данным id не найдена", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}