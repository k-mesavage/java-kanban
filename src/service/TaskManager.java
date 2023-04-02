package service;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.List;
public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    SubTask addSubTask(SubTask subTask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTask();

    Task getTaskById(Task task);

    void getEpicById(Epic epic);

    void getSubTaskById(SubTask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);


    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubTasks();

    void changeTaskStatus(Task task, Status status);

    void changeSubTaskStatus(SubTask subtask, Status status);

    List<SubTask> printSubTasksForEpic(int epicId);
    List<Task> getHistory();
}
