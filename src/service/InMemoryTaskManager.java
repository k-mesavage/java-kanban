package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap();

    static HistoryManager historyManager = Managers.getDefaultHistory();

    public List<Task> getHistory() {            //The method returns a new collection based on the browsing history
        return new ArrayList<>(historyManager.getHistory());
    }

    private void generateId() {                 //The method in turn generates a number to assign the task as ID
        ++id;
    }

    @Override
    public Task addTask(String name, String description) {
        generateId();
        Task task = new Task(name, description);
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(String name, String description) {
        generateId();
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
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

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTask(){
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public Task getTaskById(Task task) {
        historyManager.add(tasks.get(task.getId()));
        return tasks.get(task.getId());
    }

    @Override
    public Epic getEpicById(Epic epic) {
        historyManager.add(epics.get(epic.getId()));

        return epics.get(epic.getId());
    }

    @Override
    public SubTask getSubTaskById(SubTask subtask){
        historyManager.add(subTasks.get(subtask.getId()));

        return subTasks.get(subtask.getId());
    }

    @Override
    public void updateTask(Integer id, String newName, String newDescription) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.get(id).setName(newName);
            tasks.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    @Override
    public void updateEpic(Integer id, String newName, String newDescription) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epics.get(id).setName(newName);
            epics.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    @Override
    public void updateSubTask(Integer id, String newName, String newDescription) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            subTasks.get(id).setName(newName);
            subTasks.get(id).setDescription(newDescription);
        } else {
            System.out.println("Нет задачи с таким ID");
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
    }

    @Override
    public void changeTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    @Override
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

    @Override
    public void printSubTasksForEpic(Epic epic) {
        ArrayList<Integer> idSubtasks = epic.getSubTaskId();
        for (Integer idSubtask : idSubtasks) {
            System.out.println(subTasks.get(idSubtask));
        }
    }
}
