package model;

import java.util.*;
public class Epic extends Task {

    final ArrayList<Integer> subTaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(Integer subTaskId){
        this.subTaskId.add(subTaskId);
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void removeSubTask(Integer subtaskIdToRemove){
        subTaskId.remove(subtaskIdToRemove);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }

    @Override
    public String toString() {
        return "Epic {" +
                "name = " + getName() +
                ", description = " + getDescription() +
                ", status = " +  getStatus() +
                ", id = " + getId() +
                "}"+ '\n';
    }
}
