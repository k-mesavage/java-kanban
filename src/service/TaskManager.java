package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    public Task addTask(String name, String description);

    public Epic addEpic(String name, String description);

    public SubTask addSubTask(String name, String description, Integer epicId);

    public ArrayList<Task> getTasks();

    public ArrayList<Epic> getEpics();

    public ArrayList<SubTask> getSubTask();

    public Task getTaskById(Task task);

    public Epic getEpicById(Epic epic);

    public SubTask getSubTaskById(SubTask subtask);

    public void updateTask(Integer id, String newName, String newDescription);

    public void updateEpic(Integer id, String newName, String newDescription);

    public void updateSubTask(Integer id, String newName, String newDescription);

    public void deleteTaskById(int id);

    public void deleteEpicById(int id);

    public void deleteSubTaskById(int id);

    public void deleteTasks();

    public void deleteEpics();

    public void deleteSubTasks();

    public void changeTaskStatus(Task task, Status status);

    public void changeSubTaskStatus(SubTask subtask, Status status);

    public void printSubTasksForEpic(Epic epic);
    List<Task> getHistory();
}
