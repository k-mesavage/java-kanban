package model;


import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    private String name;
    private String description;
    private Status status = Status.NEW;
    private int id;
    private static int i = 0;
    public Duration duration;
    public LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        id += ++i;
    }
    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null || duration != null) {
            assert startTime != null;
            return startTime.plusSeconds(duration.toSeconds());
        } else {
        return null;
    }}

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task {"  +
                "id = " + getId() +
                ", name = " + getName()  +
                ", description = " + getDescription() +
                ", status = " + getStatus() +
                ", startTime = " + getStartTime() +
                ", duration = " + getDuration() +
                "}"+ '\n';
    }
}