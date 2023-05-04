package test;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private InMemoryHistoryManager manager = new InMemoryHistoryManager();
    private Task task = new Task("Task", "task");
    private Epic epic = new Epic("Epic", "epic");
    private SubTask subTask = new SubTask("SubTask", "subTask", epic.getId());

    @Test
    void getEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }                                                                       //Return the empty list

    @Test
    void doubleResultInHistory() throws IOException {
        manager.add(epic);
        manager.add(epic);
        assertEquals(1, manager.getHistory().size());
    }                                                                       //Correct history size after double get

    @Test
    void deleteFromBeginning() throws IOException {                         //Correct delete
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(task.getId());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void deleteFromMiddle() throws IOException {
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(epic.getId());
        assertEquals(2, manager.getHistory().size());
    }
    @Test
    void deleteFromRear() throws IOException {
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(subTask.getId());
        assertEquals(2, manager.getHistory().size());
    }
}