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
        //Work strings
        String taskName = "taskName";
        String epicName = "epicName";
        String epicName2 = "epicName2";
        String subTaskName = "subTaskName";
        String description = "description";
        //Methods of adding tasks
        Task task1 =  manager.addTask(new Task(taskName,description));
        Task task2 = manager.addTask(new Task(taskName,description));
        Epic epic1 = manager.addEpic(new Epic(epicName,description));
        SubTask subTask1 =  manager.addSubTask((new SubTask(subTaskName, description, epic1.getId())));
        SubTask subTask2 = manager.addSubTask(new SubTask(subTaskName, description, epic1.getId()));
        Epic epic2 = manager.addEpic(new Epic(epicName, description));
        SubTask subTask3 = manager.addSubTask(new SubTask(subTaskName, description, epic2.getId()));
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Change of statuses
        manager.changeSubTaskStatus(subTask1 , Status.DONE);
        manager.changeSubTaskStatus(subTask2, Status.DONE);
        manager.changeTaskStatus(task2, Status.IN_PROGRESS);
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Delete by id
        manager.deleteTaskById(1);
        manager.deleteEpicById(6);
        manager.deleteSubTaskById(7);
        //result check
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTask());
        //Shadow implementation of add to browsing history method
        manager.getTaskById(task1);
        manager.getTaskById(task2);
        manager.getEpicById(epic1);
        manager.getEpicById(epic2);
        manager.getSubTaskById(subTask1);
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
