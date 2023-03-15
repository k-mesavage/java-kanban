package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    public Task addTask(Task task);

    public Epic addEpic(Epic epic);

    public SubTask addSubTask(SubTask subTask);

    public ArrayList<Task> getTasks();

    public ArrayList<Epic> getEpics();

    public ArrayList<SubTask> getSubTask();

    public Task getTaskById(Task task);

    public Epic getEpicById(Epic epic);

    public SubTask getSubTaskById(SubTask subtask);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(SubTask subTask);


    public void deleteTaskById(int id);

    public void deleteEpicById(int id);

    public void deleteSubTaskById(int id);

    public void deleteTasks();

    public void deleteEpics();

    public void deleteSubTasks();

    public void changeTaskStatus(Task task, Status status);

    public void changeSubTaskStatus(SubTask subtask, Status status);

    public List<SubTask> printSubTasksForEpic(int epicId);
    List<Task> getHistory();
}
