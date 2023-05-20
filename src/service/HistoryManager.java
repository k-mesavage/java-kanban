package service;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    Node get(int id);
    void remove(int id);
    List<Task> getHistory();
    void cleanHistory();
}
