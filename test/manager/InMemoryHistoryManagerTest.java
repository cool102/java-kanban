package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void prepareFixture() {
        manager = Managers.getDefault();
        task1 = new Task("task 1", "Description 1");
        task2 = new Task("task 2", "Description 2");
        task3 = new Task("task 3", "Description 3");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
    }

    @Test
    public void checkLastViewTaskInHistoryManager() {
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size(), "История должна содержать три задачи");
        Assertions.assertEquals(history.getFirst(), task3, "Последняя просмотренная задача не находится в начале списка просмотра");

    }

    @Test
    public void checkThatHistoryManagerClearsHistory() {
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        manager.clearTasks();
        List<Task> history = manager.getHistory();
        Assertions.assertFalse(history.isEmpty(), "История должна быть пустой после очистки задач");
    }

    @Test
    public void checkThatHistoryManagerRemovesTask() {
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());
        manager.removeTask(task2.getId());
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size(), "История должна содержать три задачи после удаления");
        Assertions.assertTrue(history.contains(task2), "История должна содержать удалённую задачу");
    }

    @Test
    public void checkThatHistoryManagerSavesTasksInOrder() {
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(3, history.size(), "История должна содержать три задачи");
    }

    @Test
    public void checkThatHistoryManagerSaveChangedTask() {
        Task beforeChange = manager.getTaskById(task1.getId());
        beforeChange.setDescription("new Description");
        Task changedTask = manager.updateTask(beforeChange);
        manager.getTaskById(changedTask.getId());
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(1, history.size(), "В истории просмотров более 1 задачи");
    }
}