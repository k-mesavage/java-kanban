package test;

import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import model.Status;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;
    private SubTask subTask;
    private SubTask subTask1;

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = Managers.getFileBackedTasksManager(new File("testLog.csv"));
        epic = taskManager.addEpic(new  Epic("EpicName", "Epic description"));
        subTask = taskManager.addSubTask(
                new SubTask("SubTask name", "SubTask description", epic.getId(), LocalDateTime.now(), Duration.ofHours(1)));
        subTask1 = taskManager.addSubTask(
                new SubTask("SubTask name", "SubTask description", epic.getId(), LocalDateTime.now().plusDays(1), Duration.ofHours(1)));

    }
    @Test
    void formationOfStatusWithAnEmptyListOfSubtasks() throws IOException {
        taskManager.deleteSubTasks();
        assertEquals(Status.NEW, epic.getStatus(), "Epic status != NEW!");
    }

    @Test
    void formationOfStatusForNewSubtasksTest() {
        assertEquals(Status.NEW, epic.getStatus(), "Epic status != NEW!");
    }

    @Test
    void formationOfStatusForDoneSubtasksTest() {
        taskManager.changeSubTaskStatus(subTask, Status.DONE);
        taskManager.changeSubTaskStatus(subTask1, Status.DONE);
        assertEquals(Status.DONE, epic.getStatus(), "Epic status != DONE!");
    }

    @Test
    void formationOfStatusForNewAndDoneSubtasksTest() {
        taskManager.changeSubTaskStatus(subTask, Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status != IN_PROGRESS!");
    }

    @Test
    void formationOfStatusForInProgressSubtasksTest() {
        taskManager.changeSubTaskStatus(subTask, Status.IN_PROGRESS);
        taskManager.changeSubTaskStatus(subTask1, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status != IN_PROGRESS!");
    }
}