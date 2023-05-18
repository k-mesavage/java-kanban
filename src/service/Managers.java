package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVServer;
import service.adapter.DurationAdapter;
import service.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public interface Managers {
    static TaskManager getDefault(String port, boolean isLoad) throws IOException, InterruptedException {
        return new HttpTaskManager(port, isLoad);
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
