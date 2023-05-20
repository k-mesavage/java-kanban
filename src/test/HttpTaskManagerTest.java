package test;

import model.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Task;
import server.KVServer;
import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;
    private HttpTaskManager httpTaskManager;

    @BeforeEach
    public void start() {
        try {
            this.server = new KVServer();
            this.server.start();
            this.manager = Managers.getDefault();
            manager.deleteSubTasks();
            manager.deleteTasks();
            manager.deleteEpics();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        try {
            Task task1 = manager
                    .addTask(new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1)));
            Task task2 = manager
                    .addTask(new Task("Task", "D", LocalDateTime.now().plusDays(1), Duration.ofHours(1)));
            manager.getTaskById(task1.getId());
            manager.getTaskById(task2.getId());
            httpTaskManager = new HttpTaskManager("8078", true);
            List<Task> list = httpTaskManager.getHistory();
            assertEquals(manager.getTasks(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldLoadEpics() {
        try {
            Epic epic1 = manager.addEpic(new Epic("Epic1", "D"));
            Epic epic2 = manager.addEpic(new Epic("Epic2", "D"));
            manager.getEpicById(epic1.getId());
            manager.getEpicById(epic2.getId());
            List<Task> list = manager.getHistory();
            assertEquals(manager.getEpics(), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldLoadSubtasks() {
        try {
            Epic epic1 = manager.addEpic(new Epic("Epic3", "D"));
            SubTask subtask1 = manager.addSubTask(new SubTask("SubTask", "D", epic1.getId()
                    , LocalDateTime.now().plusDays(2), Duration.ofHours(1)));
            SubTask subtask2 = manager.addSubTask(new SubTask("SubTask", "D", epic1.getId(),
                    LocalDateTime.now().plusDays(3), Duration.ofHours(1)));
            manager.getSubTaskById(subtask1.getId());
            manager.getSubTaskById(subtask2.getId());
            assertEquals(3, manager.getHistory().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}