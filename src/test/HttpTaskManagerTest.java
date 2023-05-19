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

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer server;
    private TaskManager manager;

    @BeforeEach
    public void createManager() {
        try {
            this.server = new KVServer();
            this.server.start();
            this.manager = Managers.getDefault();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() throws IOException {
        Task task1 = new Task("Task", "D", LocalDateTime.now(), Duration.ofHours(1));
        Task task2 = new Task("Task", "D", LocalDateTime.now().plusDays(1), Duration.ofHours(1));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getTasks(), list);
    }

    @Test
    public void shouldLoadEpics() throws IOException {
        Epic epic1 = new Epic("Epic", "D");
        Epic epic2 = new Epic("Epic", "D");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() throws IOException {
        Epic epic1 = new Epic("Epic", "D");
        SubTask subtask1 = manager.addSubTask(new SubTask("SubTask", "D", epic1.getId()
                , LocalDateTime.now().plusDays(2), Duration.ofHours(1)));
        SubTask subtask2 = manager.addSubTask(new SubTask("SubTask", "D", epic1.getId(),
                LocalDateTime.now().plusDays(3), Duration.ofHours(1)));
        manager.getSubTaskById(subtask1.getId());
        manager.getSubTaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getSubTasks(), list);
    }

}