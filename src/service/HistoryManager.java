package service;

import model.SubTask;
import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(SubTask id);
    List<Task> getHistory();
}
