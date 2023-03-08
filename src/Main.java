import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 =  manager.addTask("taskName1", "description");
        Task task2 = manager.addTask("taskName2", "description");
        Epic epic1 = manager.addEpic("epicName1","description");
        SubTask subtask1 = manager.addSubTask("subTaskOfEpic1", "description", epic1.getId());
        SubTask subtask2 = manager.addSubTask("subTaskOfEpic1", "description", epic1.getId());
        Epic epic2 = manager.addEpic("epicName2","description");
        SubTask subtask3 = manager.addSubTask("subTaskOfEpic2", "description", epic2.getId());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());

        manager.changeSubTaskStatus(subtask1, Status.DONE);
        manager.changeSubTaskStatus(subtask2, Status.DONE);
        manager.changeTaskStatus(task2, Status.IN_PROGRESS);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());

        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
        manager.deleteSubTaskById(7);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
    }
}
