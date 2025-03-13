package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;
import task.TaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManager3 {
    private InMemoryTaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        epic = new Epic(1, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        taskManager.addEpic(epic);
    }

    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.NEW, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.evaluateEpicStatus(epic, epic.getSubtasksIds());
        assertEquals(TaskStatus.NEW, epic.getTaskStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.DONE, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.DONE, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.evaluateEpicStatus(epic, epic.getSubtasksIds());
        assertEquals(TaskStatus.DONE, epic.getTaskStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    void shouldSetEpicStatusToInProgressWhenSubtasksAreNewAndDone() {
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.DONE, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.evaluateEpicStatus(epic, epic.getSubtasksIds());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldSetEpicStatusToInProgressWhenAllSubtasksAreInProgress() {
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.IN_PROGRESS, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.IN_PROGRESS, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.evaluateEpicStatus(epic, epic.getSubtasksIds());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus(), "Статус эпика должен быть IN_PROGRESS");
    }
}
