package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "task = Subtask {" + "epicId = " + this.epicId + " , id=" + this.getId() + "," +
                "name=" + this.getName() + ", description=" + this.getDescription() +
                ",status=" + this.getTaskStatus() +
                "} ";
    }
}
