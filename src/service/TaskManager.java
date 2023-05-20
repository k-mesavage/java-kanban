package service;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    Task addTask(Task task) throws IOException;

    Epic addEpic(Epic epic) throws IOException;

    SubTask addSubTask(SubTask subTask) throws IOException;

    ArrayList<Task> getTasks() throws IOException;

    ArrayList<Epic> getEpics() throws IOException;

    ArrayList<SubTask> getSubTasks() throws IOException;

    Task getTaskById(int id) throws IOException;

    Epic getEpicById(int id) throws IOException;

    SubTask getSubTaskById(int id) throws IOException;

    void updateTask(int id, Task task) throws IOException;

    void updateEpic(int id, Epic epic) throws IOException;

    void updateSubTask(int id, SubTask subTask) throws IOException;


    void deleteTaskById(int id) throws IOException;

    void deleteEpicById(int id) throws IOException;

    void deleteSubTaskById(int id) throws IOException;

    void deleteTasks() throws IOException;

    void deleteEpics() throws IOException;

    void deleteSubTasks() throws IOException;

    void changeTaskStatus(Task task, Status status);

    void changeSubTaskStatus(SubTask subtask, Status status);

    List<SubTask> printSubTasksForEpic(int epicId);

    List<Task> getHistory();
    void cleanHistory();

    TreeSet<Task> sortByDateTime();

    void intersectionElimination(Task newTask);
}

