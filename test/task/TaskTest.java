package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    TaskManager manager = Managers.getDefault();

    @Test
    void checkTaskEquals() {
        Task task1 = new Task("some","some");
        Task task2 = new Task("some","some");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void checkSubTaskEquals() {
        Subtask subtask1 = new Subtask("some","some", TaskStatus.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask("some","some", TaskStatus.NEW, 2);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic Task", "Description of Epic");
        Epic addedEpic = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask Task1", "Description of Subtask1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask Task2", "Description of Subtask2", TaskStatus.NEW, epic.getId());
        manager.addTask(subtask1);
        manager.addTask(subtask2);
        Assertions.assertFalse(addedEpic.getSubtasksIds().contains(addedEpic.getId()));
    }

    @Test
    public void testSubCannotBeItselfEpic() {
        Epic epic = new Epic("Epic Task", "Description of Epic");
        Epic addedEpic = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask Task1", "Description of Subtask1", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask Task2", "Description of Subtask2", TaskStatus.NEW, epic.getId());
        Assertions.assertFalse(subtask1.getEpicId() == subtask1.getId());
    }
}