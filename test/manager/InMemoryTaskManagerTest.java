package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

class InMemoryTaskManagerTest {
    private static TaskManager manager;
    private static Task task1 ;
    private static Task task2 ;
    private static Task addedTask1;
    private static Task addedTask2;
    private static Epic epic1;
    private static Epic addedEpic;
    private static  Subtask subtask1 ;
    private static  Subtask subtask2 ;
    private static  Subtask addedSubtask1;
    private static  Subtask addedSubtask2;

    @BeforeAll
    public static void prepareFixture() {
        manager = Managers.getDefault();
        task1 = new Task("task 1", "simple task 1");
        task2 = new Task("task 1", "task 2 with status ", TaskStatus.NEW);
        addedTask1 = manager.addTask(task1);
        addedTask2 = manager.addTask(task2);
        epic1 = new Epic("epic 1", "simple epic 1");
        addedEpic = manager.addEpic(epic1);
        subtask1 = new Subtask("subtask 1", "simple subtask 1", TaskStatus.NEW, addedEpic.getId());
        subtask2 = new Subtask("subtask 2", "simple subtask 2", TaskStatus.NEW, addedEpic.getId());
        addedSubtask1 = manager.addSubtask(subtask1);
        addedSubtask2 = manager.addSubtask(subtask2);
    }

    @Test
    public void checkTaskManagerWork() {
        Assertions.assertTrue(manager.getSubtasks().size() == 2, "Список субтасков не равен ожидаемому");
        Assertions.assertTrue(manager.getTasks().size() == 2,"Список субтасков не равен ожидаемому");
        Assertions.assertTrue(manager.getEpics().size() == 1,"Список субтасков не равен ожидаемому");
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()), addedTask1, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getTaskById(addedTask2.getId()), addedTask2, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getSubtaskById(subtask1.getId()), addedSubtask1, "Добавленная и полученная задачи не равны");
        Assertions.assertEquals(manager.getSubtaskById(subtask2.getId()), addedSubtask2, "Добавленная и полученная задачи не равны");
    }

    @Test
    public void checkUnmutableOfTask() {
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()).getName(), task1.getName(), "После добавления в БД задача изменилась");
        Assertions.assertEquals(manager.getTaskById(addedTask1.getId()).getDescription(), task1.getDescription(),  "После добавления в БД задача изменилась");
    }
}