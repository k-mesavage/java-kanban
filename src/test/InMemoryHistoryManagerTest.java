package test;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager manager;
    private final Task task = new Task("Task", "task");
    private final Epic epic = new Epic("Epic", "epic");
    private final SubTask subTask = new SubTask("SubTask", "subTask", epic.getId());

    @BeforeEach
    void beforeEach(){
        this.manager = new InMemoryHistoryManager();
    }

    @Test
    void getEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }                                                                       //Return the empty list

    @Test
    void doubleResultInHistory() {
        manager.add(epic);
        manager.add(epic);
        assertEquals(1, manager.getHistory().size());
    }                                                                       //Correct history size after double get

    @Test
    void deleteFromBeginning() {                         //Correct delete
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(task.getId());
        assertNull(manager.get(task.getId()));
        assertNotNull(manager.get(epic.getId()));
        assertNotNull(manager.get(subTask.getId()));
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    void deleteFromMiddle() {
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(epic.getId());
        assertNull(manager.get(epic.getId()));
        assertNotNull(manager.get(task.getId()));
        assertNotNull(manager.get(subTask.getId()));
        assertEquals(2, manager.getHistory().size());
    }
    @Test
    void deleteFromRear() {
        manager.add(task);
        manager.add(epic);
        manager.add(subTask);
        manager.remove(subTask.getId());
        assertNull(manager.get(subTask.getId()));
        assertNotNull(manager.get(task.getId()));
        assertNotNull(manager.get(epic.getId()));
        assertEquals(2, manager.getHistory().size());
    }
}