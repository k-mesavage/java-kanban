package model;

import java.util.*;

import static model.Status.*;

public class Epic extends Task {

    final ArrayList<SubTask> subTasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(SubTask subTaskId){
        this.subTasksList.add(subTaskId);
    }

    public ArrayList<SubTask> getSubTasksList() {
        return subTasksList;
    }

    public void removeSubTask(SubTask subtaskIdToRemove){
        subTasksList.remove(subtaskIdToRemove);
    }

    public void setStatus() {
        int countDoneStatus = 0;
        int countNewStatus = 0;
        for (SubTask subTask : subTasksList) {
            if (subTask.getStatus().equals(IN_PROGRESS)) {
                setStatus(IN_PROGRESS);
            }
            if (subTask.getStatus().equals(DONE)) {
                countDoneStatus++;
            }
            if (subTask.getStatus().equals(NEW)) {
                countNewStatus++;
            }
        }
        if (countDoneStatus == subTasksList.size() && !subTasksList.isEmpty()) {
            setStatus(DONE);
        } else if (countDoneStatus > 0 && countDoneStatus < subTasksList.size()) {
            setStatus(IN_PROGRESS);
        } else if (subTasksList.isEmpty() || countNewStatus == subTasksList.size()) {
            setStatus(NEW);
        }
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
                "id = " + getId() +
                ", name = " + getName() +
                ", description = " + getDescription() +
                ", status = " +  getStatus() +
                "}"+ '\n';
    }
}
