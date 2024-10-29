import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        out.println("Поехали!");
        TaskManager manager = new TaskManager();
        out.println("TEST 1 - add task");

        out.println("создадим 2 сущности ТАСК ");
        Task task1 = new Task("купить хлеб", " ", TaskStatus.NEW);
        out.println("Создал ТАСК\n" + task1);
        Task task2 = new Task("купить сыр", " ", TaskStatus.NEW);
        out.println("Создал ТАСК2\n" + task2);
        out.println("add 2 tasks");
        manager.addTask(task1);
        manager.addTask(task2);

        out.println("\n TEST2 - Печатаю все ТАСКИ\n" + manager.getTasks());
        out.println("print created tesks---->" + manager.getTasks());
        out.println();

        out.println("\nTEST 3 - change task \n");
        task2.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task2);
        out.println();
        out.println("Обновил ТАСК2\n" + task2);
        out.println();


        out.println("\nTEST 4 - get  task by id \n");
        out.println("get task by id2:" + manager.getTaskById(task2.getId()));
        out.println();
        out.println("\nTEST 4.1 - remove task by id");
        manager.removeTask(task1.getId());
        out.println(manager.getTasks());
        out.println("there is " + manager.getTasks().size() + " tasks in manager service");

        out.println("\nTEST 5 - delete all tasks \n");
        out.println("clear all tasks ...");
        manager.clearTasks();
        out.println("print all tasks" + manager.getTasks());
        out.println();

        out.println("\nTEST 6 - add epic \n");
        Epic epic1 = new Epic("Сдать сессию", "");
        manager.addEpic(epic1);
        out.println("\nget all epics\n" + manager.getEpics());

        out.println("\nTEST 7 - add subtask \n");
        Subtask subtask11 = new Subtask("Сдать экзамен 1", "", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Сдать экзамен 2", " ", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask12);

//        Subtask subtask2 = new Subtask("Набрать лекарство в шприц", " ", TaskStatus.NEW, null);
//        manager.addSubtask(subtask2);

        out.println("\nget all subtasks\n" + manager.getSubtasks());
        out.println();
        out.println("\nTEST 8 - get all subtasks by epic id \n");
        out.println("all subtasks by epic id:");
        List<Subtask> allSubTasksByEpicId = manager.getAllSubTasksByEpicId(epic1.getId());
        out.println(allSubTasksByEpicId);
        out.println();
        out.println("get subtask by id 1: " + manager.getSubtaskById(subtask11.getId()));
        out.println("get subtask by id 2: " + manager.getSubtaskById(subtask12.getId()));
        out.println();
        out.println(epic1);

        out.println("\nTEST 9 - update subtask 1, look how epic status in changed\n" + manager.getSubtasks());
        subtask11.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask11);
        out.println(subtask11);
        out.println(epic1);


        out.println("\nTEST 10 - update subtask 2, look how epic status in changed\n" + manager.getSubtasks());
        subtask12.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask12);
        out.println(subtask12);
        out.println(epic1);

        out.println("\nTEST 11 - update epic 1\n" + manager.getEpicById(epic1.getId()));
        Epic epicU = new Epic("Сдать сессию досрочно", "");
        manager.updateEpic(epicU);
        out.println(manager.getEpicById(epic1.getId()));


        out.println("\nTEST 12 -    subtask 1 - new," + "subtask 2 - new. " + "Remove subtask 2 \n");
        Epic epic2 = new Epic("Перепрыгнуть через овраг", "");
        manager.addEpic(epic2);
        out.println(epic2);

        Subtask subtask21 = new Subtask("Разогнаться", "", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(subtask21);
        out.println(subtask21);
        Subtask subtask22 = new Subtask("Прыгнуть", " ", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(subtask22);
        out.println(subtask22);
        out.println("print new epic");
        out.println(epic2);
        out.println("change both subtask to done...");
        subtask21.setTaskStatus(TaskStatus.DONE);
        subtask22.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask21);
        manager.updateSubtask(subtask22);
        out.println("epic 2 is DONE " + epic2);
        out.println("change subtask 2 to IN_PROGRESS");
        subtask22.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask22);
        out.println("epic 2 is IN PROGRESS " + epic2);
        out.println("remove subtask 2");
        manager.removeSubtask(subtask22.getId());
        out.println("epic 2 is DONE again " + epic2);

        out.println("\nTEST 13 -    subtask 1 - new," + "subtask 2 - new. " + "Remove subtask 2 \n");
        Epic epic3 = new Epic("ПРиготовить хлеб", "");
        manager.addEpic(epic3);
        out.println(epic3);

        Subtask subtask31 = new Subtask("приготовить тесто", "", TaskStatus.NEW, epic3.getId());
        manager.addSubtask(subtask31);
        out.println(subtask31);
        Subtask subtask32 = new Subtask("В печь на 1 час", " ", TaskStatus.NEW, epic3.getId());
        manager.addSubtask(subtask32);
        out.println(subtask32);
        out.println("print new epic");
        out.println(epic3);

        out.println("change subtask32 to done...");
        subtask32.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask32);
        out.println("subtask31 = " + subtask31);
        out.println("epic 3 is IN PROGRESS " + epic3);
        out.println("subtask31 = " + subtask31);
        out.println("remove subtask 32");
        manager.removeSubtask(subtask32.getId());
        out.println("subtask31 = " + subtask31);
        out.println("epic 3 is NEW again " + epic3);
    }
}
