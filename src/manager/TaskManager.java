package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {

    Task addTask(Task newTask);

    Subtask addSubtask(Subtask subtask);

    Epic addEpic(Epic epic);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    List<Subtask> getAllSubTasksByEpicId(int epicId);

    Task updateTask(Task updateTask);

    Subtask updateSubtask(Subtask forUpdate);

    Epic updateEpic(Epic updateEpic);

    void removeTask(int id);

    void removeSubtask(int id);

    void removeEpic(int id);

    void clearSubtasks();

    void clearEpics();

    void clearTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}