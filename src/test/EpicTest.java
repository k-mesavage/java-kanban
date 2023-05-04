package test;

import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import model.Status;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;
    private SubTask subTask;
    private SubTask subTask1;


    @BeforeEach
    void beforeEach() throws IOException {
        taskManager = Managers.getDefault();
        epic = new Epic("EpicName", "Epic description");
        subTask = new SubTask("SubTask name", "SubTask description", epic.getId());
        subTask1 = new SubTask("SubTask name", "SubTask description", epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask1);

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
    void formationOfStatusForDoneSubtasksTest() throws IOException {
        taskManager.changeSubTaskStatus(subTask, Status.DONE);
        taskManager.changeSubTaskStatus(subTask1, Status.DONE);
        assertEquals(Status.DONE, epic.getStatus(), "Epic status != DONE!");
    }

    @Test
    void formationOfStatusForNewAndDoneSubtasksTest() throws IOException {
        taskManager.changeSubTaskStatus(subTask, Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status != IN_PROGRESS!");
    }

    @Test
    void formationOfStatusForInProgressSubtasksTest() throws IOException {
        taskManager.changeSubTaskStatus(subTask, Status.IN_PROGRESS);
        taskManager.changeSubTaskStatus(subTask1, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status != IN_PROGRESS!");
    }
}