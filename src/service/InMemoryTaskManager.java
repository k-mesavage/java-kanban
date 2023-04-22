package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 0;
    protected static final HashMap<Integer, Task> tasks = new HashMap<>();
    protected static final HashMap<Integer, Epic> epics = new HashMap<>();
    protected static final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    static final HistoryManager historyManager = Managers.getDefaultHistory();


    public List<Task> getHistory() throws IOException {            //The method returns a new collection based on the browsing history
        return new LinkedList<>(historyManager.getHistory());
    }

    private int generateId() {                 //The method in turn generates a number to assign the task as ID
        ++id;
        return id;
    }

    @Override
    public Task addTask(Task task) throws IOException {
        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) throws IOException {
        int id = generateId();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) throws IOException {
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
    public ArrayList<SubTask> getSubTask() {
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public Task getTaskById(int id) throws IOException {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpicById(int id) throws IOException {
        historyManager.add(epics.get(id));
        Task epic = epics.get(id);
        return epic;
    }

    @Override
    public Task getSubTaskById(int id) throws IOException {
        historyManager.add(subTasks.get(id));
        Task subTask = subTasks.get(id);
        return subTask;
    }

    @Override
    public void updateTask(Task task) throws IOException {
        tasks.get(id).setName(task.getName());
        tasks.get(id).setDescription(task.getDescription());
    }

    @Override
    public void updateEpic(Epic epic) throws IOException {
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) throws IOException {
        SubTask subtask = subTasks.get(subTask.getId());
        subTasks.get(subtask.getId()).setName(subtask.getName());
        subTasks.get(subtask.getId()).setDescription(subtask.getDescription());
    }

    @Override
    public void deleteTaskById(int id) throws IOException {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws IOException {
        Epic epic = epics.get(id);
        for (Integer subtaskId : epic.getSubTaskId()) {
            subTasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) throws IOException {
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
    public void deleteTasks() throws IOException {
        tasks.clear();
    }

    @Override
    public void deleteEpics() throws IOException {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() throws IOException {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTaskId().clear();
            updateEpic(epic);
        }
    }

    @Override
    public void changeTaskStatus(Task task, Status status) throws IOException {
        task.setStatus(status);
    }

    @Override
    public void changeSubTaskStatus(SubTask subtask, Status status) throws IOException {
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
    public List<SubTask> printSubTasksForEpic(int epicId) throws IOException {
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
