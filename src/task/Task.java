package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/***
 * Class defines a Task;
 */
public class Task {
    protected int id;
    protected TaskStatus taskStatus;
    protected String name;
    protected String description;
    protected TaskType taskType;
    protected int epicId;
    protected transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Duration duration;
    LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.taskStatus = status;
    }

    public Task(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.taskStatus = status;
        this.description = description;
        this.epicId = epicId;
    }

    public Task(int id, TaskType taskType, String name, TaskStatus status, String description, int epicId, long duration, String startTime) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.taskStatus = status;
        this.description = description;
        this.epicId = epicId;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Task(TaskType taskType, String name, TaskStatus status, String description, int epicId, long duration, String startTime) {
        this.taskType = taskType;
        this.name = name;
        this.taskStatus = status;
        this.description = description;
        this.epicId = epicId;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Task(TaskType taskType, String name, TaskStatus status, String description, int epicId) {
        this.taskType = taskType;
        this.name = name;
        this.taskStatus = status;
        this.description = description;
        this.epicId = epicId;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        //String durationStr = (duration != null) ? duration.toHours() + ":" + duration.toMinutesPart() : "N/A";
        String durationStr = (duration != null) ? String.valueOf(duration.toMinutes()) : "N/A";
        String startTimeStr = (startTime != null) ? getStartTime().format(formatter) : "N/A";
        String endTimeStr = (getEndTime() != null) ? getEndTime().format(formatter) : "N/A";
        return id + "," + taskType + "," + name + "," + taskStatus + "," + description + "," + epicId + "," + durationStr + "," + startTimeStr + "," + endTimeStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (this.getClass() != o.getClass()) {
            return false;
        }
        Task other = (Task) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}