package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "task = Epic{" +
                "subtasksIds=" + subtasksIds +
                ", taskStatus=" + taskStatus +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                "} ";
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }
}
