import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    /*Welcome to the Test Class Interface.
    This class implements methods with parameters
        for a console check of the program's performance.*/
    public static void main(String[] args) throws IOException {
        //Create defaultManager (InMemoryTaskManager);
        TaskManager manager = Managers.getDefault();
        //Work strings
        String taskName = "taskName";
        String taskName2 = "taskName2";
        String epicName = "epicName";
        String epicName2 = "epicName2";
        String subTaskName = "subTaskName";
        String description = "description";
        //Methods of adding tasks
        Task task1 =  manager.addTask(new Task(taskName,description, LocalDateTime.now(), Duration.ofHours(1)));
        Task task3 = new Task("new", "new");
        Epic epic1 = manager.addEpic(new Epic(epicName,description));
        Epic epic2 = manager.addEpic(new Epic(epicName2, description));
        SubTask subTask1 =  manager.addSubTask((new SubTask(subTaskName, description, epic1.getId()
                , LocalDateTime.now().plusDays(1), Duration.ofHours(1))));
        manager.changeSubTaskStatus(subTask1, Status.DONE);
        System.out.println(epic1.getStatus());
        //manager.deleteSubTaskById(subTask1.getId());
        manager.updateSubTask(subTask1.getId(), new SubTask("new", "new", epic1.getId(),
                subTask1.getStartTime(), subTask1.getDuration()));
        SubTask s = manager.addSubTask(new SubTask(taskName2, description, epic1.getId(),
                LocalDateTime.of(2022,2,2, 2,22)
                , Duration.ofHours(1).plusMinutes(30)));
        System.out.println(manager.getSubTaskById(s.getId()));
        System.out.println(manager.getSubTasks());



        //Calling tasks to check history;
        /*manager.getEpicById(epic1.getId());                 //calling the first object
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getSubTaskById(subTask1.getId());           //calling the second object
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getEpicById(epic1.getId());                 //recalling the first object, shift down in history
        System.out.println(manager.getHistory());
        System.out.println();
        manager.getEpicById(epic2.getId());                 //related objects removed
        manager.deleteEpicById(epic1.getId());
        System.out.println(manager.getHistory());*/
    }
}
