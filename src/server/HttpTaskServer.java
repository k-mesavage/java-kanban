package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;


import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8078;
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault();
    private final Gson gson = Managers.getGson();
    

    public HttpTaskServer() throws IOException, InterruptedException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/history", this::history);
        server.createContext("/tasks/", this::tasks);
        server.createContext("/tasks/task", this::task);
        server.createContext("/tasks/epic", this::epic);
        server.createContext("/tasks/subtask", this::subTask);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private void history(HttpExchange httpExchange) {
        String requestMethod = httpExchange.getRequestMethod();
        JsonObject history = new JsonObject();
        try (httpExchange) {
            if (requestMethod.equals("GET")) {
                JsonArray historyList = new JsonArray();
                history.add("history", historyList);
                for (Task task : manager.getHistory()) {
                    historyList.add(task.getId());
                }
                sendText(httpExchange, history.toString());
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void tasks(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        JsonArray taskList = new JsonArray();
        JsonObject tasks = new JsonObject();
        try (httpExchange) {
            if (requestMethod.equals("GET")) {
                tasks.add("tasks", taskList);
                for (Task task : manager.getTasks()) {
                    taskList.add(String.valueOf(task));
                }
                for (Epic epic : manager.getEpics()) {
                    taskList.add(String.valueOf(epic));
                }
                for (SubTask subTask : manager.getSubTasks()) {
                    taskList.add(String.valueOf(subTask));
                }
                sendText(httpExchange, tasks.toString());
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void task(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        Integer id = parsePathId(httpExchange.getRequestURI());
        try (httpExchange) {
            switch (requestMethod) {
                case "GET" -> {
                    if (id != -1) {
                        Task task = manager.getTaskById(id);
                        String response = gson.toJson(task);
                        sendText(httpExchange, response);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
                case "POST" -> {
                    Task task = gson.fromJson(JsonParser.parseString(read(httpExchange)), Task.class);
                    manager.addTask(task);
                    httpExchange.sendResponseHeaders(200, 0);
                }
                case "DELETE" -> {
                    if (id != -1) {
                        manager.deleteTaskById(id);
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println("Задача " + id + " удалена");
                    } else {
                        manager.deleteTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        System.out.println("Все задачи удалены");
                    }
                }
                default -> httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void epic(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        Integer id = parsePathId(httpExchange.getRequestURI());
        try (httpExchange) {
            switch (requestMethod) {
                case "GET":
                    if (id != -1) {
                        sendText(httpExchange, gson.toJson(manager.getEpicById(id)));
                    }
                case "POST":
                    String body = read(httpExchange);
                    Epic epic = gson.fromJson(JsonParser.parseString(body), Epic.class);
                    manager.addEpic(epic);
                    for (SubTask subTask : epic.getSubTasksList()) {
                        manager.addSubTask(subTask);
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                case "DELETE":
                    if (id != null) {
                        manager.deleteEpicById(id);
                    } else {
                        manager.deleteEpics();
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    public void subTask(HttpExchange httpExchange) throws IOException {
        Integer id = parsePathId(httpExchange.getRequestURI());
        String requestMethod = httpExchange.getRequestMethod();
        try (httpExchange) {
            switch (requestMethod) {
                case "GET" -> {
                    if (id != -1) {
                        sendText(httpExchange, gson.toJson(manager.getSubTaskById(id)));
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                }
                case "POST" -> {
                    SubTask subTask = gson.fromJson(JsonParser.parseString(read(httpExchange)), SubTask.class);
                    manager.addSubTask(subTask);
                    httpExchange.sendResponseHeaders(200, 0);
                }
                case "DELETE" -> {
                    if (id != -1) {
                        manager.deleteSubTaskById(id);
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                }
                default -> httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server2 = new KVServer();
        HttpTaskServer server1 = new HttpTaskServer();
        TaskManager manager = server1.manager;
        manager.addTask(new Task("T", "D", LocalDateTime.now(), Duration.ofHours(1)));
        manager.addTask(new Task("T", "D", LocalDateTime.now().plusDays(1), Duration.ofHours(1)));
        Epic epic = manager.addEpic(new Epic("Epic", "Description"));
        manager.addSubTask(new SubTask(
                "S", "d", epic.getId(), LocalDateTime.now().plusHours(5), Duration.ofHours(1)));

        server1.start();
    }

    private String read (HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }
    private Integer parsePathId (URI uri){
        if (uri.getQuery() != null) {
            String[] split = uri.getQuery().split("&");
            for (String s1 : split) {
                String name = s1.split("=")[0];
                String value = s1.split("=")[1];
                if (name.equals("id")) {
                    return Integer.parseInt(value);
                }
            }
        }
        return -1;
    }

    protected void sendText(HttpExchange server, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        server.getResponseHeaders().add("Content-Type", "application/json");
        server.sendResponseHeaders(200, resp.length);
        server.getResponseBody().write(resp);
    }

    public TaskManager getManager() {
        return manager;
    }
}
