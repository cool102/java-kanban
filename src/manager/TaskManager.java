package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class TaskManager {
    private static int taskCount = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();


    public TaskManager() {

    }

    private int generateTaskId() {
        return ++taskCount;
    }

    public int addTask(Task newTask) {
        newTask.setId(generateTaskId());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int addSubtask(Subtask subtask) {
        int subtaskId = generateTaskId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        Integer epicId = subtask.getEpicId();
        if (epicId != null) {
            Epic epic = epics.get(epicId);
            epic.getSubtasksIds().add(subtaskId);
            return subtaskId;
        } else {
            throw new RuntimeException("cant add subtask" + subtask + " because epic id = null");
        }

    }

    public int addEpic(Epic epic) {
        epic.setId(generateTaskId());
        epic.setTaskStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
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

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }


    public List<Subtask> getAllSubTasksByEpicId(int epicId) {
        List<Subtask> allSubtaskOfEpic = new ArrayList<>();
        for (Subtask sub : getSubtasks()) {
            if ((sub.getEpicId() == epicId)) {
                allSubtaskOfEpic.add(sub);
            }
        }
        return allSubtaskOfEpic;
    }

    public void updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
    }

    public void updateSubtask(Subtask forUpdate) {
        int epicId = forUpdate.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        TaskStatus epicStatus = evaluateEpicStatus(subtasksIds);
        Epic epicById = getEpicById(epicId);
        epicById.setTaskStatus(epicStatus);
        subtasks.put(forUpdate.getId(), forUpdate);
    }

    public void updateEpic(Epic updateEpic, int id) {
        Epic forUpdate = getEpicById(id);
        forUpdate.setName(updateEpic.getName());
        forUpdate.setDescription(updateEpic.getDescription());
        epics.put(forUpdate.getId(), forUpdate);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask forRemove = getSubtaskById(id);
        int epicId = forRemove.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        subtasksIds.remove(Integer.valueOf(id));
        TaskStatus epicStatus = evaluateEpicStatus(subtasksIds);
        Epic epicById = getEpicById(epicId);
        epicById.setTaskStatus(epicStatus);
        subtasks.remove(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
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


    private TaskStatus evaluateEpicStatus(List<Integer> subtasksIds) {
        int size = subtasksIds.size();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtasksId : subtasksIds) {
            Subtask subtask = subtasks.get(subtasksId);
            subtasksOfEpic.add(subtask);
        }
        if (subtasksOfEpic.isEmpty()) {
            return TaskStatus.NEW;
        }
        long subtasksInDone = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.DONE)
                .count();
        long subtasksInNew = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.NEW)
                .count();
        if (subtasksInDone == size) {
            return TaskStatus.DONE;
        } else if (subtasksInNew == size) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }


}