package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class TaskManager {
    private int taskCount = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();


    public TaskManager() {

    }

    private int generateTaskId() {
        return ++taskCount;
    }

    public Task addTask(Task newTask) {
        newTask.setId(generateTaskId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Subtask addSubtask(Subtask subtask) {
        int subtaskId = generateTaskId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        Integer epicId = subtask.getEpicId();
        if (epicId != null) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtasksIds().add(subtaskId);
                updateEpic(epic, epic.getId());
                return subtask;
            } else {
                throw new RuntimeException("epic not exist, because epic = null");
            }
        } else {
            throw new RuntimeException("cant add subtask" + subtask + " because epic id = null");
        }

    }

    public Epic addEpic(Epic epic) {
        epic.setId(generateTaskId());
        epic.setTaskStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().toList();
    }

    public List<Epic> getEpics() {
        return epics.values().stream().toList();
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

    public Task updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    public Subtask updateSubtask(Subtask forUpdate) {
        int epicId = forUpdate.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        TaskStatus epicStatus = evaluateEpicStatus(subtasksIds);
        epic.setTaskStatus(epicStatus);
        subtasks.put(forUpdate.getId(), forUpdate);
        return forUpdate;
    }

    public Epic updateEpic(Epic updateEpic, int id) {
        Epic forUpdate = getEpicById(id);
        forUpdate.setName(updateEpic.getName());
        forUpdate.setDescription(updateEpic.getDescription());
        epics.put(forUpdate.getId(), forUpdate);
        return forUpdate;
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = getSubtaskById(id);
        int epicId = subtask.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        subtasksIds.remove(Integer.valueOf(id));
        TaskStatus epicStatus = evaluateEpicStatus(subtasksIds);
        Epic epicById = getEpicById(epicId);
        epicById.setTaskStatus(epicStatus);
        subtasks.remove(id);
    }

    public void removeEpic(int id) {
        Epic epic = getEpicById(id);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        for (Integer subtasksId : subtasksIds) {
            subtasks.remove(subtasksId);
        }
        epics.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void clearEpics() {
        List<Epic> all = getEpics();
        for (Epic current : all) {
            List<Integer> subtasksIds = current.getSubtasksIds();
            for (Integer subtasksId : subtasksIds) {
                removeTask(subtasksId);
            }
        }
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