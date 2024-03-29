package service;

import model.*;
import service.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    protected File file;
    protected static final String HEAD = ("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager(String port) {
        this.file =new File(port);
    }
    public FileBackedTasksManager() {
    }


        @Override
    public void changeTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    public static void main(String[] args) throws IOException {
        FileBackedTasksManager xo = new FileBackedTasksManager          /*ADD USERNAME*/
                (new File("/Users/USERNAME/Documents/dev/java-kanban/src/service/resources/log.csv"));

//Set up different tasks, epics, and subtasks.
        Task task = xo.addTask(new Task("TaskOne", "TaskDescription"));
        Epic epic = xo.addEpic(new Epic("EpicOne", "EpicDescription"));
        SubTask subTask1 = xo.addSubTask(new SubTask("SubTaskOne", "SubTaskDesc", epic.getId()));
        SubTask subTask2 = xo.addSubTask(new SubTask("SubTaskTwo", "SubTaskDesc", epic.getId()));

        System.out.println(xo.getTasks());

//Request some of them to complete your browsing history.
        xo.getTaskById(task.getId());
        xo.getEpicById(epic.getId());
        xo.getSubTaskById(subTask1.getId());

//Create a new FileBackedTasksManager manager from the same file.
        FileBackedTasksManager test = new FileBackedTasksManager(
                new File("/Users/USERNAME/Documents/dev/java-kanban/src/service/resources/log.csv"));
        test.loadFromFile(new File("/Users/USERNAME/Documents/dev/java-kanban/src/service/resources/log.csv"));

//FileBackedTasksManager recovered correctly?
        System.out.println(test.getHistory());
        System.out.println(test.getTasks());
        System.out.println(test.getEpics());
    }
    public void loadFromFile (File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                if (!line.equals(HEAD)) {
                    if (fromString(line) instanceof Epic epic) {
                        addEpic(epic);
                    } else if (fromString(line) instanceof SubTask subTask) {
                        addSubTask(subTask);
                    } else {
                        Task task = fromString(line);
                        addTask(task);
                    }
                }
            }

            String historyLine = reader.readLine();
            for (int id : historyFromString(historyLine)) {
                if (tasks.containsKey(id)) {
                    historyManager.add(tasks.get(id));
                } else if (epics.containsKey(id)) {
                    historyManager.add(epics.get(id));
                } else if (subTasks.containsKey(id)) {
                    historyManager.add(subTasks.get(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException
                    ("File read error, check dir: /Users/USERNAME/Documents/dev/java-kanban/src/service/resources!");
        }
    }
    void save() throws IOException {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException
                    ("Save file error, check dir /Users/USERNAME/Documents/dev/java-kanban/src/service/resources!");
        }
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEAD);
            writer.write(System.lineSeparator());
            for (Task task : getTasks()) {
                writer.write(toString(task) + System.lineSeparator());
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + System.lineSeparator());
                for (SubTask subTask : getSubTasks()) {
                    writer.write(toString(subTask)+ System.lineSeparator());
                }
            }
            writer.write(System.lineSeparator());
            writer.write(historyToString());}

        }
    private static Task fromString(String value) {
        String[] params = value.split(",");
        Integer id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        String description = params[4];
        String status = params[3];
        if (type.equals("EPIC")) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(Status.valueOf(status));
            return epic;
        } else if (type.equals("SUBTASK")) {
            int epicId = Integer.parseInt(params[5]);
            SubTask subTask = new SubTask(name, description, epicId);
            subTask.setId(id);
            subTask.setStatus(Status.valueOf(status));
            return subTask;
        } else {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(Status.valueOf(status));
            return task;
        }
    }

    static String historyToString() {
        List<Task> history = InMemoryTaskManager.historyManager.getHistory();
        List<String> idTasks = new ArrayList<>();
        for (Task task : history) {
            idTasks.add(String.valueOf(task.getId()));
        }
        return String.join(",", idTasks);
    }
    static List<Integer> historyFromString(String line) {
        List<Integer> list = new ArrayList<>();
        if (line != null) {

            String[] ids = line.split(",");
            for (int i = ids.length - 1; i >= 0; i--) {
                list.add(Integer.parseInt(ids[i]));
            }
            return list;
        }
        return list;
    }
    private static String getEpicId(Task task) {
        if (task instanceof SubTask) {
            return Integer.toString(((SubTask) task).getEpicId());
        }
        return null;
    }
    private static Type getType(Task task) {
        if (task instanceof Epic) {
            return Type.EPIC;
        } else if (task instanceof SubTask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
    }
    private static String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getName(),
                task.getStatus().toString(), task.getDescription(), getEpicId(task)};
        return String.join(",", toJoin);
    }
    @Override
    public Epic getEpicById(int id) throws IOException {
        super.getEpicById(id);
        save();
        return super.getEpicById(id);
    }
    @Override
    public SubTask getSubTaskById(int id) throws IOException {
        super.getSubTaskById(id);
        save();
        return super.getSubTaskById(id);
    }
    @Override
    public void updateTask(int id, Task task) throws IOException {
        super.updateTask(id, task);
        save();
    }
    @Override
    public void updateEpic(int id, Epic epic) throws IOException {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) throws IOException {
        super.updateSubTask(id, subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws IOException {
        super.deleteTaskById(id);
        save();
    }
    @Override
    public void deleteEpicById(int id) throws IOException {
        super.deleteEpicById(id);
        save();
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
        save();
    }
    @Override
    public void deleteTasks() throws IOException {
        super.deleteTasks();
        save();
    }
    @Override
    public void deleteEpics() throws IOException {
        super.deleteEpics();
        save();
    }
    @Override
    public void deleteSubTasks() throws IOException {
        super.deleteSubTasks();
        save();
    }
}



