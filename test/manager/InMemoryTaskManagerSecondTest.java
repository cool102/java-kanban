package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.File;

public class InMemoryTaskManagerSecondTest {
    private static TaskManager manager;
    private static Task task1;
    private static Epic epic1;
    private static Subtask subtask1;
    private static Subtask subtask2;
    //private final File file = new File("savedTasksWithTime");

    @BeforeEach
    public void prepareFixture() {
        manager = Managers.getDefault();
        task1 = new Task(TaskType.TASK, "task name", TaskStatus.NEW, "task description", 999, 180, "2000-01-01 01:00");
        subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.NEW, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        epic1 = new Epic(TaskType.EPIC, "epic 1", TaskStatus.NEW, "simple epic 1", -1);
    }

    @Test
    public void testTaskManager() {
        manager.addEpic(epic1);
        manager.addTask(task1);
        Assertions.assertThrows(RuntimeException.class, () -> manager.addSubtask(subtask1));
    }

    @Test
    public void addTaskToManagerTestWithOverlapping() {

        Task task1 = new Task(TaskType.TASK, "Задача 1", TaskStatus.NEW, "Описание", 0,
                60, "2025-03-13 10:00");

        Task task2 = new Task(TaskType.TASK, "Задача 2", TaskStatus.NEW, "Описание", 0,
                30, "2025-03-14 10:30");

        Task task3 = new Task(TaskType.TASK, "Задача 3", TaskStatus.NEW, "Описание", 0,
                45, "2025-03-15 11:30");

        System.out.println(manager.addTask(task1)); // true
        System.out.println(manager.addTask(task2)); // false (пересекается)
        System.out.println(manager.addTask(task3)); // true

    }

}
