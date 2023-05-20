package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVServer;
import service.adapter.DurationAdapter;
import service.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public interface Managers {
    static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("8078", true);
    }
    static FileBackedTasksManager getFileBackedTasksManager(File file) {
        return new FileBackedTasksManager(file);
    }

    static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    static Gson getGson() {
       GsonBuilder gsonBuilder = new GsonBuilder();
       gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
       gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
       return gsonBuilder.create();
    }
}
