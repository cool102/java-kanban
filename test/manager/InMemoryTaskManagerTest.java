package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

class InMemoryTaskManagerTest {
    private static TaskManager manager;
    private static Task task1;
    private static Task task2;
    private static Task task3;
    private static Task addedTask1;
    private static Task addedTask2;
    private static Epic epic1;
    private static Epic addedEpic;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask addedSubtask1;
    private static Subtask addedSubtask2;

    @BeforeEach
    public void prepareFixture() {
        manager = Managers.getDefault();
        task1 = new Task(1,TaskType.TASK, "Задача 1", TaskStatus.NEW, "Описание", 0,
                60, "2025-03-13 10:00");
        task2 = new Task(2,TaskType.TASK, "Задача 2", TaskStatus.NEW, "Описание", 0,
                60, "2025-03-14 10:00");
        task3 = new Task(TaskType.TASK, "Задача 3", TaskStatus.NEW, "Описание", 0,
                60, "2025-03-15 10:00");
        task3.setId(999);
        addedTask1 = manager.addTask(task1);
        addedTask2 = manager.addTask(task2);
        epic1 = new Epic(TaskType.EPIC, "epic 1", TaskStatus.NEW, "simple epic 1", -1);
        addedEpic = manager.addEpic(epic1);
        subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 3, 180, "2000-01-01 01:00");
        subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2  name", TaskStatus.NEW, "subtask 2 description", 3, 180, "2000-01-03 01:00");
        addedSubtask1 = manager.addSubtask(subtask1);
        addedSubtask2 = manager.addSubtask(subtask2);
    }

    @Test
    public void checkTaskIdsNotConflict() {
        int manualCreatedTaskId = task3.getId();
        int idGeneratedByManager = addedTask1.getId();
        Assertions.assertNotEquals(manualCreatedTaskId, idGeneratedByManager);
    }

    @Test
    public void checkTaskManagerWork() {
        Assertions.assertEquals(2, manager.getSubtasks().size(), "Список субтасков не равен ожидаемому");
        Assertions.assertEquals(2, manager.getTasks().size(), "Список субтасков не равен ожидаемому");
        Assertions.assertEquals(1, manager.getEpics().size(), "Список эпиков не равен ожидаемому");
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()), addedTask1, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getTaskById(addedTask2.getId()), addedTask2, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getSubtaskById(subtask1.getId()), addedSubtask1, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getSubtaskById(subtask2.getId()), addedSubtask2, "Добавленная и полученная задачи не равны");
    }

    @Test
    public void checkUnmutableOfTask() {
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()).getName(), task1.getName(), "После добавления в БД задача изменилась");
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()).getDescription(), task1.getDescription(), "После добавления в БД задача изменилась");
    }
}