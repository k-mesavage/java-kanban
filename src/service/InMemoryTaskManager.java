package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap();

    static HistoryManager historyManager = Managers.getDefaultHistory();

    public List<Task> getHistory() {            //The method returns a new collection based on the browsing history
        return new LinkedList<>(historyManager.getHistory());
    }

    private int generateId() {                 //The method in turn generates a number to assign the task as ID
        ++id;
        return id;
    }

    @Override
    public Task addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        int id = generateId();
        subTask.setId(id);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTask(subTask.getId());
        epics.put(subTask.getEpicId(), epic);
        return subTask;
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
    public void updateTask(Task task) {
        tasks.get(id).setName(task.getName());
        tasks.get(id).setDescription(task.getDescription());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask subtask = subTasks.get(subTask.getId());
        subTasks.get(subtask.getId()).setName(subtask.getName());
        subTasks.get(subtask.getId()).setDescription(subtask.getDescription());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubTaskId()) {
            subTasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubTaskId().remove(subtask.getId());
            updateEpic(epic);
            subTasks.remove(id);
        } else {
            System.out.println("Subtask not found");
        }
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskId().clear();
            updateEpic(epic);
        }
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
    public List<SubTask> printSubTasksForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            List<SubTask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(epicId);
            for (int i = 0; i < epic.getSubTaskId().size(); i++) {
                subtasksNew.add(subTasks.get(epic.getSubTaskId().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }
}
