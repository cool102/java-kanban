import java.util.*;

public class TaskManager {
    private static int taskCount = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();


    public TaskManager() {

    }

    public int generateTaskId() {
        return ++taskCount;
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public int addTask(Task newTask) {
        newTask.setId(generateTaskId());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int addSubtask(Subtask subtask) {
        subtask.setId(generateTaskId());
        subtasks.put(subtask.getId(), subtask);
        return subtask.getId();
    }

    public int addEpic(Epic epic) {
        epic.setId(generateTaskId());
        epic.setTaskStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
    }

    public void updateSubtask(Subtask forUpdate) {
        int epicId = forUpdate.getEpicId();
        List<Subtask> allSubtasksOfEpic = getAllSubTasksByEpicId(epicId);
        TaskStatus epicStatus = evaluateEpicStatus(allSubtasksOfEpic);
        Epic epicById = getEpicById(epicId);
        epicById.setTaskStatus(epicStatus);
        subtasks.put(forUpdate.getId(), forUpdate);
    }

    private List<Subtask> getAllSubTasksByEpicId(int epicId) {
        List<Subtask> allSubtaskOfEpic = new ArrayList<>();
        for (Subtask sub : getSubtasks()) {
            if ((sub.getEpicId() == epicId)) {
                allSubtaskOfEpic.add(sub);
            }
        }
        return allSubtaskOfEpic;
    }

    private TaskStatus evaluateEpicStatus(List<Subtask> subtasks) {
        int size = subtasks.size();
        long subtasksInDone = subtasks.stream().filter(s -> s.getTaskStatus() == TaskStatus.DONE)
                .count();
        long subtasksInNew = subtasks.stream().filter(s -> s.getTaskStatus() == TaskStatus.NEW)
                .count();
        if (subtasksInDone == size) {
            return TaskStatus.DONE;
        } else if (subtasksInNew == size) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }
}