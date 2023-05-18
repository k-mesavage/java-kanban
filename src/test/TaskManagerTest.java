package test;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.InMemoryTaskManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<H extends FileBackedTasksManager> {

    InMemoryTaskManager manager;

    @BeforeEach
    void beforeEach() {
        this.manager = new InMemoryTaskManager();
    }
    @Test
    void addTask() {
        Task task = manager.addTask(new Task("Task", "task"));
        assertNotNull(task);                                                    //Task add
    }

    @Test
    void addEpic() {
        Epic epic = manager.addEpic(new Epic("Epic", "epic"));
        assertNotNull(epic);                                                    //Epic add
    }

    @Test
    void addSubTask() throws IOException {
        Epic epic = manager.addEpic(new Epic("Epic", "epic"));
        SubTask subTask = manager.addSubTask(new SubTask("SubTask", "subtask", epic.getId()));
        assertNotNull(manager.getSubTasks());                          // get SubTask
        assertEquals(epic.getId(), manager.getSubTaskById(subTask.getId()).getEpicId()); //correct EpicId

    }

    @Test
    void getTasks() {
        manager.addTask(new Task("Task", "task"));
        System.out.println(manager.getTasks());
        assertEquals(1, manager.getTasks().size());             //Tasks list correct size
    }

    @Test
    void getEpics() {
        manager.addEpic(new Epic("Epic", "epic"));
        assertEquals(1, manager.getEpics().size());             //Epics list correct size
    }

    @Test
    void getSubTasks() throws IOException {
        Epic epic = manager.addEpic(new Epic("Epic", "epic"));
        manager.addSubTask(new SubTask("SubTask", "subtask", epic.getId()));
        manager.addSubTask(new SubTask("SubTask", "subtask", epic.getId()));
        assertEquals(2, manager.getSubTasks().size());          //SubTasks list correct size
    }

    @Test
    void getTaskById() {
        Task task = manager.addTask(new Task("Task", "task"));
        assertEquals(task, manager.getTaskById(task.getId()));             //Returns the correct Task
        assertThrows(NullPointerException.class, ()-> manager.getTaskById(777));  //Incorrect TaskId
    }

    @Test
    void getEpicById() throws IOException {
        Epic epic =  manager.addEpic(new Epic("Epic", "epic"));
        Epic epic1 = manager.getEpicById(epic.getId());
        assertEquals(epic, epic1);                                          //Returns the correct Epic
        assertThrows(NullPointerException.class, ()-> manager.getEpicById(777));    //Incorrect EpicId
    }

    @Test
    void getSubTaskById() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        SubTask subTask = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        assertNotNull(manager.getSubTaskById(subTask.getId()));             //Returns the correct SubTask
        assertThrows(NullPointerException.class, ()-> manager.getSubTaskById(777));    //Incorrect SubTaskId
    }

    @Test
    void updateTask() throws IOException {
        Task task = manager.addTask(new Task("Task", "task"));
        manager.updateTask(task.getId(), new Task("new", "new"));
        assertEquals("new", manager.getTaskById(task.getId()).getName());
    }                                                                       //Changes correct

    @Test
    void updateEpic() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        manager.updateEpic(epic.getId(), new Epic("new", "new"));
        assertEquals("new", manager.getEpicById(epic.getId()).getName());
    }                                                                       //Changes correct

    @Test
    void updateSubTask() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        SubTask subTask = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        manager.updateSubTask(subTask.getId(), new SubTask("new", "new", epic.getId()));
        assertEquals("new", manager.getSubTaskById(subTask.getId()).getName());
    }                                                                       //Changes correct

    @Test
    void deleteTaskById() throws IOException {
        Task task = manager.addTask(new Task("Task", "task"));
        manager.deleteTaskById(task.getId());
        assertThrows(NullPointerException.class, () ->manager.getTaskById(task.getId()));
    }                                                                       //NullPointerException for getTaskById

    @Test
    void deleteEpicById() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        manager.deleteEpicById(epic.getId());
        assertTrue(manager.getEpics().isEmpty());
    }                                                                       //Return the void

    @Test
    void deleteSubTaskById() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        SubTask subTask = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        manager.deleteSubTaskById(subTask.getId());
        assertTrue(manager.getSubTasks().isEmpty());
    }                                                                       //Return the void

    @Test
    void deleteTasks() throws IOException {
        manager.addTask(new Task("Task", "task"));
        manager.deleteTasks();
        assertEquals(0, manager.getTasks().size());
    }                                                                       //Return the void

    @Test
    void deleteEpics() throws IOException {
        manager.addEpic(new Epic("Task", "task"));
        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size());
    }                                                                       //Return the void

    @Test
    void deleteSubTasks() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        manager.deleteSubTasks();
        assertEquals(0, manager.getSubTasks().size());
    }                                                                       //Return the void

    @Test
    void changeTaskStatus() {
        Task task = manager.addTask(new Task("Task", "task"));
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }                                                                       //Changes correct

    @Test
    void changeSubTaskStatus() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        SubTask subTask = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        subTask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, subTask.getStatus());
    }                                                                       //Changes correct

    @Test
    void changeEpicStatus() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        SubTask subTask = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        SubTask subTask1 = manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        assertEquals(Status.NEW, epic.getStatus());                                      //NEW for new SubTasks
        manager.changeSubTaskStatus(subTask, Status.IN_PROGRESS);
        manager.changeSubTaskStatus(subTask1, Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus()); //IN_PROGRESS for IN_PROGRESS
        manager.changeSubTaskStatus(subTask, Status.DONE);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus()); //IN_PROGRESS for DONE/NEW
        manager.changeSubTaskStatus(subTask1, Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());        //DONE for DONE/DONE
    }

    @Test
    void printSubTasksForEpic() throws IOException {
        Epic epic = manager.addEpic(new Epic("Task", "task"));
        manager.addSubTask(new SubTask("Task", "task", epic.getId()));
        assertEquals(1, manager.printSubTasksForEpic(epic.getId()).size());
    }                                                                       //Return the list
    @Test
    void getHistory() {
        assertNotNull(manager.getHistory());
        Task task = manager.addTask(new Task("Task1", "task"));
        manager.getTaskById(task.getId());
        assertNotNull(manager.getHistory());
    }                                                                       //Return the list
    @Test
    void sortByDateTime() {
        manager.addTask(new Task("NoTimeTask", "description"));
        manager.addTask(new Task("LastTask", "description",
                LocalDateTime.of(2022, 5, 5, 10,20), Duration.ofHours(1)));
        manager.addTask(new Task("FirstTask", "description",
                LocalDateTime.of(2022, 5, 4, 10,20), Duration.ofHours(1)));
        TreeSet<Task> prioritizedTasks =  manager.sortByDateTime();
        int counter = 1;
        for (Task task : prioritizedTasks) {
            if(counter == 1) {
                assertEquals("FirstTask", task.getName());
            }
            if(counter == 2) {                                              //sorted by DateTime
                assertEquals("LastTask", task.getName());
            }
            if(counter == 3) {
                assertEquals("NoTimeTask", task.getName());
            }
            counter++;
        }
    }

    @Test
    void intersectionElimination() {
        manager.addTask(new Task("Task", "description",
                LocalDateTime.of(2022, 6, 4, 12,20), Duration.ofHours(1)));
        assertThrows(IllegalArgumentException.class
                ,() -> manager.addTask(new Task("Task", "description",
                        LocalDateTime.of(2022, 6, 4, 12,21), Duration.ofHours(1))));
    }                                                                       //IllegalArgumentException
}