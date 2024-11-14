package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    private static TaskManager manager;
    private static Task task;
    private static Task addedTask;


    @BeforeAll
    public static void prepareFixture() {
        manager = Managers.getDefault();
        task = new Task("task 1", "Description");
        addedTask = manager.addTask(task);
    }

    @Test
    public void checkThatHistoryManagerSaveChangedTask() {
        Task beforeChange = manager.getTaskById(addedTask.getId());
        beforeChange.setDescription("new Description");
        Task changedTask = manager.updateTask(beforeChange);
        manager.getTaskById(changedTask.getId());
        List<Task> history = manager.getHistoryManager().getHistory();
        Task taskBeforeChange = history.get(0);
        Task taskAfterChange = history.get(1);
        Assertions.assertEquals(taskBeforeChange.getDescription(), taskAfterChange.getDescription(), "Одна и та же задача в истории просмотра имеет разные описания");
    }
}