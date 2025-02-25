package task;

import java.util.ArrayList;
import java.util.List;

/***
 * Class define Epic;
 */
public class Epic extends Task {
    private final List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, TaskType taskType, String name, TaskStatus taskStatus, String description, int epicId) {
        super(id, taskType, name, taskStatus, description, epicId);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }
}
