package task;

public class Subtask extends Task {

    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId) {
        super(id, taskType, name, status, description, epicId);
    }

    public Integer getEpicId() {
        return epicId;
    }
}
