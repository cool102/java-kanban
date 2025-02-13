package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

public class InMemoryHistoryManagerTest2 {
    private TaskManager manager;
    private Task addedTask;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void prepareFixture() {
        manager = new InMemoryTaskManager();
        Task task = new Task("task 1", "Description");
        addedTask = manager.addTask(task);
        epic = manager.addEpic(new Epic("Epic 1", "Epic description"));
        subtask = manager.addSubtask(new Subtask("Subtask 1", "Subtask description", TaskStatus.NEW, epic.getId()));
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
