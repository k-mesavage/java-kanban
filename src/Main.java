import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.*;

public class Main {

    /*Welcome to the Test Class Interface.
    This class implements methods with parameters
        for a console check of the program's performance.*/
    public static void main(String[] args) {
        //Create defaultManager (InMemoryTaskManager);
        TaskManager manager = Managers.getDefault();
        //Methods of adding tasks
        Task task1 =  manager.addTask("taskName1", "description");
        Task task2 = manager.addTask("taskName2", "description");
        Epic epic1 = manager.addEpic("epicName1","description");
        SubTask subtask1 = manager.addSubTask("subTaskOfEpic1", "description", epic1.getId());
        SubTask subtask2 = manager.addSubTask("subTaskOfEpic1", "description", epic1.getId());
        Epic epic2 = manager.addEpic("epicName2","description");
        SubTask subtask3 = manager.addSubTask("subTaskOfEpic2", "description", epic2.getId());
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Change of statuses
        manager.changeSubTaskStatus(subtask1, Status.DONE);
        manager.changeSubTaskStatus(subtask2, Status.DONE);
        manager.changeTaskStatus(task2, Status.IN_PROGRESS);
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Shadow implementation of add to browsing history method
        manager.getEpicById(epic1);
        manager.getTaskById(task1);
        //Delete by id
        manager.deleteTaskById(1);
        manager.deleteEpicById(2);
        manager.deleteSubTaskById(7);
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Delete all
        manager.deleteEpics();
        manager.deleteTasks();
        manager.deleteSubTasks();
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //History browsing
        System.out.println(manager.getHistory());
    }
}
