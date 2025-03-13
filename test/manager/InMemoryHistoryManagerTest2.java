package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

public class InMemoryHistoryManagerTest2 {
    private TaskManager manager;
    private Task addedTask;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void prepareFixture() {
        manager = new InMemoryTaskManager();
        Task task = new Task(TaskType.TASK, "task name", TaskStatus.NEW, "task description", 999, 180, "2000-01-01 01:00");
        addedTask = manager.addTask(task);
        epic = manager.addEpic(new Epic(TaskType.EPIC, "epic 1", TaskStatus.NEW, "simple epic 1", -1));
        subtask = manager.addSubtask(new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.NEW, "subtask 2 description", epic.getId(), 60, "2000-01-02 02:00"));
    }

    @Test
    public void checkLinkedListOperations() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(addedTask);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size(), "История должна содержать 3 задачи");

        historyManager.remove(subtask.getId());
        history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size(), "История должна содержать 3 задачи после удаления подзадачи");
    }

    @Test
    public void checkEpicsDoNotContainInvalidSubtaskIds() {
        manager.removeSubtask(subtask.getId());
        Epic updatedEpic = manager.getEpicById(epic.getId());
        Assertions.assertFalse(updatedEpic.getSubtasksIds().contains(subtask.getId()), "Эпик не должен содержать ID удаленной подзадачи");
    }

    @Test
    public void checkTaskFieldChangeDoesNotCorruptManager() {
        Task newTask = manager.addTask(new Task("Task 2", "Description 2"));
        newTask.setId(999);
        Task retrievedTask = manager.getTaskById(999);
        Assertions.assertNull(retrievedTask, "Менеджер не должен содержать задачу с измененным ID");
    }
}
