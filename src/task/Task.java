package task;

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

@Override
public String toString() {
    return id + "," + taskType + "," + name + "," + taskStatus + "," + description + "," + epicId;
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