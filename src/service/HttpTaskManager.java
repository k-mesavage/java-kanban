package service;

import com.google.gson.*;
import server.KVTaskClient;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(String port, boolean isLoad) throws IOException {
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
        JsonElement jsonTasks = JsonParser.parseString(client.load("tasks"));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.addTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load("epics"));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic task = gson.fromJson(jsonEpic, Epic.class);
                this.addEpic(task);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load("subtasks"));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                SubTask task = gson.fromJson(jsonSubtask, SubTask.class);
                this.addSubTask(task);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load("history"));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subTasks.containsKey(taskId)) {
                    this.getSubTaskById(taskId);
                } else if (this.epics.containsKey(taskId)) {
                    this.getEpicById(taskId);
                } else if (this.tasks.containsKey(taskId)) {
                    this.getTaskById(taskId);
                }
            }
        }
    }
}