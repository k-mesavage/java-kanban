package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import server.KVTaskClient;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(String port, boolean isLoad) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8078), 0);
        server.start();
        client = new KVTaskClient(port);
        if (isLoad) {
            load();
        }
    }

    public HttpTaskManager(String port) throws IOException {
        this(port, false);
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(getTasks()));
        client.put("epics", gson.toJson(getEpics()));
        client.put("subtasks", gson.toJson(getSubTasks()));
        client.put("history", gson.toJson(historyManager.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

    private void load() throws IOException {
        String jsonTask = client.load("tasks");
        if (!jsonTask.isEmpty()) {
            tasks = gson.fromJson(jsonTask,
                    new TypeToken<Map<Integer, Task>>() {
                    }.getType());
        }

        String jsonEpic = client.load("epics");
        if (!jsonEpic.isEmpty()) {
            epics = gson.fromJson(jsonEpic,
                    new TypeToken<Map<Integer, Epic>>() {
                    }.getType());
        }

        String jsonSubtask = client.load("subtasks");
        if (!jsonSubtask.isEmpty()) {
            subTasks = gson.fromJson(jsonSubtask,
                    new TypeToken<Map<Integer, SubTask>>() {
                    }.getType());
        }

        String jsonHistory = client.load("history");
        List<Integer> history = gson.fromJson(jsonHistory, new TypeToken<List<Integer>>() {
        }.getType());

        if (history != null) {
            for (Integer id : history) {
                if (epics.containsKey(id)) {
                    Epic epic = epics.get(id);
                    getEpicById(epic.getId());
                }
                if (tasks.containsKey(id)) {
                    Task task1 = tasks.get(id);
                    getTaskById(task1.getId());
                }
                if (subTasks.containsKey(id)) {
                    SubTask subtask = subTasks.get(id);
                    getSubTaskById(subtask.getId());
                }
            }
        }
    }
}