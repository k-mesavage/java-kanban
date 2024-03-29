package test;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Test
    void loadFromEmptyFile() {
        File file = new File("testLog.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        assertDoesNotThrow(
                ()-> {
        Writer writer = new FileWriter("testLog.csv");
        writer.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC" + "/n");
        fileBackedTasksManager.loadFromFile(file);});
    }
    @Test
    void loadFromFile() throws IOException {
        File file = new File("testLog.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        Task task = fileBackedTasksManager.addTask(new Task("Task", "task"));
        Epic epic = fileBackedTasksManager.addEpic(new Epic("Epic", "epic"));
        fileBackedTasksManager.loadFromFile(new File("testLog.csv"));
        assertEquals(task, fileBackedTasksManager.getTaskById(task.getId()));
        assertEquals(epic, fileBackedTasksManager.getEpicById(epic.getId()));
    }

}