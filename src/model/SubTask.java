package model;
public class SubTask extends Task {

    protected final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);

        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
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
        return "Subtask{" +
                "id = " + getId() +
                ", epicId = " + epicId +
                ", name = " + getName() +
                ", description = " + getDescription() +
                ", status = " +  getStatus() +
                '}'+ '\n';
    }
}