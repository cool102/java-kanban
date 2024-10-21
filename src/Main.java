public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        Task task1 = new Task("купить хлеб", " ",TaskStatus.NEW);
        manager.addTask(task1);

        Task task2 = new Task("купить молоко", "", TaskStatus.IN_PROGRESS);
        manager.addTask(task2);

        Epic epic1 = new Epic("Найти золото", "");
        manager.addEpic(epic1);

        Subtask subtask11 = new Subtask("Поехать на Аляску", "", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask11);

        Subtask subtask12 = new Subtask("Выкопать золото", " ", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(subtask12);

        System.out.println(epic1);

        subtask11.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask11);

        System.out.println(subtask11);
        System.out.println(epic1);



        subtask12.setTaskStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask12);

        System.out.println(subtask12);
        System.out.println(epic1);
    }
}
