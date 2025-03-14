package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId) {
        super(id, taskType, name, status, description, epicId);
    }

    public Subtask(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, taskType, name, status, description, epicId);
        this.duration = duration;
        this.startTime = startTime;
    }

    public Subtask(TaskType taskType, String name, TaskStatus status, String description, int epicId, long duration, String startTime) {
        super(taskType, name, status, description, epicId);
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Subtask(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId, long duration, String startTime) {
        super(id, taskType, name, status, description, epicId);
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Integer getEpicId() {
        return epicId;
    }
}
