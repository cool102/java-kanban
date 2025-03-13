package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***
 * Class define Epic;
 */
public class Epic extends Task {
    private final List<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, TaskType taskType, String name, TaskStatus taskStatus, String description, int epicId) {
        super(id, taskType, name, taskStatus, description, epicId);
    }

    public Epic(TaskType taskType, String name, TaskStatus taskStatus, String description, int epicId) {
        super(taskType, name, taskStatus, description, epicId);
    }

    public void calculateTimeAndDuration(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
            return;
        }
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;

        for (Subtask subtask : subtasks) {
            if (subtasksIds.contains(subtask.getId())) {
                if (subtask.getStartTime() != null) {
                    if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                        earliestStart = subtask.getStartTime();
                    }
                }

                LocalDateTime subtaskEndTime = subtask.getEndTime();
                if (subtaskEndTime != null) {
                    if (latestEnd == null || subtaskEndTime.isAfter(latestEnd)) {
                        latestEnd = subtaskEndTime;
                    }
                }

                if (subtask.getDuration() != null) {
                    totalDuration = totalDuration.plus(subtask.getDuration());
                }
            }
        }

        this.startTime = earliestStart;
        this.endTime = latestEnd;
        this.duration = totalDuration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }
}
