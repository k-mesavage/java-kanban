package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyList.size() == 9) {
                historyList.remove(0);
                historyList.add(task);
            } else {
                historyList.add(task);
            }
        }
    }
    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
