package service;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public interface TaskManager {

    Task addTask(Task task) throws IOException;

    Epic addEpic(Epic epic) throws IOException;

    SubTask addSubTask(SubTask subTask) throws IOException;

    ArrayList<Task> getTasks() throws IOException;

    ArrayList<Epic> getEpics() throws IOException;

    ArrayList<SubTask> getSubTask() throws IOException;

    Task getTaskById(int id) throws IOException;

    Task getEpicById(int id) throws IOException;

    Task getSubTaskById(int id) throws IOException;

    void updateTask(Task task) throws IOException;

    void updateEpic(Epic epic) throws IOException;

    void updateSubTask(SubTask subTask) throws IOException;


    void deleteTaskById(int id) throws IOException;

    void deleteEpicById(int id) throws IOException;

    void deleteSubTaskById(int id) throws IOException;

    void deleteTasks() throws IOException;

    void deleteEpics() throws IOException;

    void deleteSubTasks() throws IOException;

    void changeTaskStatus(Task task, Status status) throws IOException;

    void changeSubTaskStatus(SubTask subtask, Status status) throws IOException;

    List<SubTask> printSubTasksForEpic(int epicId) throws IOException;
    List<Task> getHistory() throws IOException;
}
