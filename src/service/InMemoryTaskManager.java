package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 1;
    protected static HashMap<Integer, Task> tasks = new HashMap<>();
    protected static HashMap<Integer, Epic> epics = new HashMap<>();
    protected static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    static final HistoryManager historyManager = Managers.getDefaultHistory();


    public List<Task> getHistory() {           //The method returns a new collection based on the browsing history
        return new LinkedList<>(historyManager.getHistory());
    }

    private int generateId() {                 //The method in turn generates a number to assign the task as ID
        ++id;
        return id;
    }

    @Override
    public TreeSet<Task> sortByDateTime() {
        return new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void intersectionElimination(Task newTask) {
        if(newTask.getStartTime() == null) {
        return;
    }
        LocalDateTime endTimeOfNewTask = newTask.getStartTime().plus(newTask.getDuration());
        for(Task task:tasks.values()) {
            if(task.getStartTime() == null) {
                continue;
            }
                if (!(task instanceof Epic)) {
                LocalDateTime endTimeOfTask = task.getStartTime().plus(task.getDuration());
                if (newTask.getStartTime().isAfter(task.getStartTime())
                        && newTask.getStartTime().isBefore(endTimeOfTask)) {
                    throw new IllegalArgumentException("There are other tasks at this time!");
                }
                if (endTimeOfNewTask.isAfter(task.getStartTime())
                        && endTimeOfNewTask.isBefore(endTimeOfTask)) {
                    throw new IllegalArgumentException("There are other tasks at this time!");
                }
                    if (endTimeOfNewTask.isBefore(task.getStartTime())
                            && endTimeOfNewTask.isAfter(endTimeOfTask)
                    && newTask.getStartTime().isBefore(task.getStartTime())
                    && newTask.getStartTime().isAfter(task.getEndTime())) {
                        throw new IllegalArgumentException("There are other tasks at this time!");
                    }
                    if (endTimeOfTask.isBefore(newTask.getStartTime())
                            && endTimeOfTask.isAfter(endTimeOfNewTask)
                    && task.getStartTime().isBefore(newTask.getStartTime())
                    && task.getStartTime().isAfter(newTask.getEndTime())) {
                        throw new IllegalArgumentException("There are other tasks at this time!");
                    }
            }
        }
    }

    @Override
    public Task addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        intersectionElimination(task);
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
    public SubTask addSubTask(SubTask subTask) throws IOException {
        int id = generateId();
        subTask.setId(id);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = getEpicById(subTask.getEpicId());
        epic.addSubTask(subTask);
        epics.put(subTask.getEpicId(), epic);
        intersectionElimination(subTask);
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
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws IOException {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) throws IOException {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    public void setTaskHashMap(HashMap<Integer, Task> taskHashMap) {
        this.tasks = taskHashMap;
    }

    public void setEpicHashMap(HashMap<Integer, Epic> epicHashMap) {
        this.epics = epicHashMap;
    }

    public void setSubtaskHashMap(HashMap<Integer, SubTask> subtaskHashMap) {
        this.subTasks = subtaskHashMap;
    }
    @Override
    public void updateTask(int id, Task task) throws IOException{
        if (tasks.get(id) == null){
            return;
        }
        intersectionElimination(task);
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(int id, Epic epic) throws IOException {
        Epic oldEpic = epics.get(id);
        if (oldEpic == null){
            return;
        }
        epic.setId(oldEpic.getId());
        epic.setSubTasksList(oldEpic.getSubTasksList());
        epics.put(id, epic);
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) throws IOException {
        intersectionElimination(subTask);
        SubTask oldSubTask = subTasks.get(id);
        subTask.setId(oldSubTask.getId());
        Epic epic = epics.get(oldSubTask.getEpicId());
        int index = epic.getSubTasksList().indexOf(oldSubTask);
        epic.getSubTasksList().set(index, subTask);
        subTasks.put(id, subTask);
        epic.setStatus();
    }

    @Override
    public void deleteTaskById(int id) throws IOException {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws IOException{
        Epic epic = epics.get(id);
        for (SubTask subtaskId : epic.getSubTasksList()) {
            subTasks.remove(subtaskId.getId());
            historyManager.remove(id);
        }
        epics.remove(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) throws IOException {
        SubTask subtask = subTasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubTasksList().remove(subtask);
            updateEpic(epic.getId(), epic);
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
            epic.getSubTasksList().clear();
            updateEpic(epic.getId(), epic);
        }
    }

    @Override
    public void changeTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    @Override
    public void changeSubTaskStatus(SubTask subtask, Status status) {
        subtask.setStatus(status);
        Epic epic = epics.get(subtask.getEpicId());
        epic.setStatus();

    }
    @Override
    public List<SubTask> printSubTasksForEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            return epic.getSubTasksList();
        } else {
            return Collections.emptyList();
        }
    }
}
