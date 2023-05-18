package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import service.adapter.LocalDateTimeAdapter;
import model.Epic;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = new GsonBuilder().
            registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManager(String port, boolean isLoad) {
        super();
        client = new KVTaskClient(port);
        if (isLoad) {
            load();
        }
    }

    public HttpTaskManager(String port) {
        this(port, false);
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(tasks));
        client.put("epics", gson.toJson(epics));
        client.put("subtasks", gson.toJson(subTasks));
        client.put("history", gson.toJson(historyManager.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

    private void load() {
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
                    historyManager.add(epic);
                }
                if (tasks.containsKey(id)) {
                    Task task1 = tasks.get(id);
                    historyManager.add(task1);
                }
                if (subTasks.containsKey(id)) {
                    SubTask subtask = subTasks.get(id);
                    historyManager.add(subtask);
                }
            }
        }
    }
}