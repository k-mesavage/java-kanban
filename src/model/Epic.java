package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static model.Status.*;

public class Epic extends Task {

    private ArrayList<SubTask> subTasksList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(SubTask subTaskId){
        this.subTasksList.add(subTaskId);
        timeCalculation();
    }

    public ArrayList<SubTask> getSubTasksList() {
        return subTasksList;
    }
    public void setSubTasksList(ArrayList<SubTask> list) {
        this.subTasksList = list;
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
    public void timeCalculation() {
        Duration duration = null;
        LocalDateTime firstDate = null;
        LocalDateTime lastDate = null;
        if (subTasksList != null){
            for (SubTask subTask : subTasksList){
                if (subTask.getDuration()!=null && subTask.getStartTime()!=null){
                    if (firstDate == null || firstDate.isAfter(subTask.getStartTime()))
                        firstDate = subTask.getStartTime();
                    if(lastDate == null || lastDate.isBefore(subTask.getEndTime()))
                        lastDate = subTask.getEndTime();
                    if (duration == null)
                        duration = subTask.getDuration();
                    else
                        duration = duration.plus(subTask.getDuration());
                }
            }
        }
        this.duration = duration;
        startTime = firstDate;
        endTime = lastDate;
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
