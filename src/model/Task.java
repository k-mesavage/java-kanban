package model;


public class Task {

    private String name;
    private String description;
    private Status status = Status.NEW;
    private int id;
    private static int i = 0;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        id += ++i;
    }

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
                "name = " + name  +
                ", description = " + description +
                ", status = " + status +
                ", id = " + id +
                "}"+ '\n';
    }
}