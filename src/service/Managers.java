package service;

public interface Managers {
    static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
