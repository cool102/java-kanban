import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("task 1","simple task 1");
        Task task2 = new Task("task 2", "task 2 with status ", TaskStatus.NEW);
        TaskManager manager = Managers.getDefault();

        manager.addTask(task1);
        manager.addTask(task2);
        Epic epic1 = new Epic("epic 1" , "simple epic 1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "simple subtask 1", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask 2", "simple subtask 2", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask2);

        System.out.println(manager.getHistory());

        manager.getTaskById(task1.getId());
        System.out.println(manager.getHistory());

        manager.getSubtaskById(subtask1.getId());
        System.out.println(manager.getHistory());

        manager.getEpicById(epic1.getId());
        System.out.println(manager.getHistory());
    }
}
