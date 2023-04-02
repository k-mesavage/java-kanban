import model.Epic;
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
        Epic epic2 = manager.addEpic(new Epic(epicName2, description));
        SubTask subTask1 =  manager.addSubTask((new SubTask(subTaskName, description, epic1.getId())));
        SubTask subTask2 = manager.addSubTask(new SubTask(subTaskName, description, epic1.getId()));
        SubTask subTask3 =  manager.addSubTask((new SubTask(subTaskName, description, epic1.getId())));
        //Calling tasks to check history;
        manager.getEpicById(epic1);
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getSubTaskById(subTask1);
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getEpicById(epic2);
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getEpicById(epic1);
        System.out.println(manager.getHistory());
        System.out.println();
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getHistory());
    }
}
