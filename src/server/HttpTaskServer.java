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
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

//Извиняюсь за повторную отправку, не смог задать вопрос в пачке
// , спасибо за более подробное разъяснение по инициализации


public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault();
    private final Gson gson = Managers.getGson();
    private final int port = 8080;

    public HttpTaskServer() throws IOException, InterruptedException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/tasks/history", this::history);
        server.createContext("/tasks/", this::tasks);
        server.createContext("/tasks/task", this::task);
        server.createContext("/tasks/epic", this::epic);
        server.createContext("/tasks/subtask", this::subTask);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public TaskManager getManager() {
        return manager;
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

    private void task(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        Integer id = parsePathId(httpExchange.getRequestURI());
        try (httpExchange) {
            switch (requestMethod) {
                case "GET" -> {
                    if (id != null) {
                        Task task = manager.getTaskById(id);
                        String response = gson.toJson(task);
                        sendText(httpExchange, response);
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
                case "POST" -> {
                    String json = read(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Пустая задача");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Task task = gson.fromJson(JsonParser.parseString(json), Task.class);
                    manager.addTask(task);
                    httpExchange.sendResponseHeaders(200, 0);
                }

                case "DELETE" -> {
                    if (id != null) {
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
                    if (id != null) {
                        sendText(httpExchange, gson.toJson(manager.getEpicById(id)));
                    } else {
                        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        sendText(httpExchange, gson.toJson(manager.getEpics()));
                    }
                case "POST":
                    String json = read(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Пустая задача");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    Epic epic = gson.fromJson(JsonParser.parseString(json), Epic.class);
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
        String requestMethod = httpExchange.getRequestMethod();
        Integer id = parsePathId(httpExchange.getRequestURI());
        try (httpExchange) {
            switch (requestMethod) {
                case "GET" -> {
                    if (id != null) {
                        if (manager.getSubTasks().stream()
                                .anyMatch(s -> Objects.equals(s.getId(), id))) {
                            String body = gson.toJson(manager.getSubTaskById(id));
                            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(200, 0);
                            sendText(httpExchange, body);
                        }
                    } else {
                        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, 0);
                        sendText(httpExchange, gson.toJson(manager.getSubTasks()));
                    }
                }
                case "POST" -> {
                    String json = read(httpExchange);
                    if (json.isEmpty()) {
                        System.out.println("Пустая задача");
                        httpExchange.sendResponseHeaders(400, 0);
                        return;
                    }
                    SubTask subTask = gson.fromJson(json, SubTask.class);
                    if (subTask.getId() == null || subTask.getId() == 0) {
                        manager.addSubTask(subTask);
                    } else {
                        manager.updateSubTask(subTask.getId(), subTask);
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                }
                case "DELETE" -> {
                    if (id != null) {
                        manager.deleteSubTaskById(id);
                    }
                    else {
                        manager.deleteSubTasks();
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                }
                default -> httpExchange.sendResponseHeaders(405, 0);
            }
        }
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
        return null;
    }

    protected void sendText(HttpExchange server, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        server.getResponseHeaders().add("Content-Type", "application/json");
        server.sendResponseHeaders(200, resp.length);
        server.getResponseBody().write(resp);
    }
}
