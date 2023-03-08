package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private Integer id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap();

    private void generateId() {
        ++id;
    }

    public Task addTask(String name, String description) {
        generateId();
        Task task = new Task(name, description);
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(String name, String description) {
        generateId();
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask addSubTask(String name, String description, Integer epicId) {
        generateId();
        SubTask subtask = new SubTask(name, description, epicId);
        subtask.setId(id);
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        epic.addSubTask(subtask.getId());
        epics.put(epicId, epic);
        return subtask;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTask(){
        return new ArrayList<>(subTasks.values());
    }


    public Task getTaskById(Task task) {
        return tasks.get(task.getId());
    }

    public Epic getEpicById(Epic epic) {
        return epics.get(epic.getId());
    }

    public SubTask getSubTaskById(SubTask subtask){
        return subTasks.get(subtask.getId());
    }

    public void updateTask(Integer id, String newName, String newDescription) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.get(id).setName(newName);
            tasks.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    public void updateEpic(Integer id, String newName, String newDescription) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epics.get(id).setName(newName);
            epics.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    public void updateSubTask(Integer id, String newName, String newDescription) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            subTasks.get(id).setName(newName);
            subTasks.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public void changeTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    public void changeSubTaskStatus(SubTask subtask, Status status){
        subtask.setStatus(status);
        epics.get(subtask.getId());
        for (Integer subtaskId : epics.get(subtask.getEpicId()).getSubTaskId()) {
            if(subTasks.get(subtaskId).getStatus() == Status.NEW){
                epics.get(subtask.getEpicId()).setStatus(Status.NEW);
            } else if (subTasks.get(subtaskId).getStatus() == Status.DONE){
                epics.get(subtask.getEpicId()).setStatus(Status.DONE);
            } else {
                epics.get(subtask.getEpicId()).setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void printSubTasksForEpic(Epic epic) {
        ArrayList<Integer> idSubtasks = epic.getSubTaskId();
        for (Integer idSubtask : idSubtasks) {
            System.out.println(subTasks.get(idSubtask));
        }
    }
}
