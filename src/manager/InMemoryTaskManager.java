package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int taskCount = 0;

    public InMemoryTaskManager() {

    }

    @Override
    public Task addTask(Task newTask) {
        if (newTask.getId() == 0) {
            newTask.setId(generateTaskId());
        }
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(generateTaskId());
        }
        subtasks.put(subtask.getId(), subtask);
        Integer epicId = subtask.getEpicId();
        if (epicId != null) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtasksIds().add(subtask.getId());
                updateEpic(epic);
                return subtask;
            } else {
                throw new RuntimeException("epic not exist, because epic = null");
            }
        } else {
            throw new RuntimeException("cant add subtask" + subtask + " because epic id = null");
        }
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(generateTaskId());
        }
        epic.setTaskStatus(TaskStatus.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Subtask> getAllSubTasksByEpicId(int epicId) {
        List<Subtask> allSubtaskOfEpic = new ArrayList<>();
        for (Subtask sub : getSubtasks()) {
            if ((sub.getEpicId() == epicId)) {
                allSubtaskOfEpic.add(sub);
            }
        }
        return allSubtaskOfEpic;
    }

    @Override
    public Task updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    @Override
    public Subtask updateSubtask(Subtask forUpdate) {
        int epicId = forUpdate.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        evaluateEpicStatus(epic, subtasksIds);
        subtasks.put(forUpdate.getId(), forUpdate);
        return forUpdate;
    }

    @Override
    public Epic updateEpic(Epic updateEpic) {
        //Epic forUpdate = getEpicById(updateEpic.getId());
        updateEpic.setName(updateEpic.getName());
        updateEpic.setDescription(updateEpic.getDescription());
        epics.put(updateEpic.getId(), updateEpic);
        return updateEpic;
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = getSubtaskById(id);
        int epicId = subtask.getEpicId();
        Epic epic = getEpicById(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        subtasksIds.remove(Integer.valueOf(id));
        Epic epicById = getEpicById(epicId);
        evaluateEpicStatus(epicById, subtasksIds);
        subtasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = getEpicById(id);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        for (Integer subtasksId : subtasksIds) {
            subtasks.remove(subtasksId);
        }
        epics.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void clearSubtasks() {
        List<Subtask> allSubtasks = getSubtasks();
        for (Subtask subtask : allSubtasks) {
            Integer epicId = subtask.getEpicId();
            Epic epic = getEpicById(epicId);
            evaluateEpicStatus(epic, epic.getSubtasksIds());
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public int generateTaskId() {
        return ++taskCount;
    }

    private Epic evaluateEpicStatus(Epic epic, List<Integer> subtasksIds) {
        int size = subtasksIds.size();
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtasksId : subtasksIds) {
            Subtask subtask = subtasks.get(subtasksId);
            subtasksOfEpic.add(subtask);
        }
        if (subtasksOfEpic.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return epic;
        }
        long subtasksInDone = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.DONE)
                .count();
        long subtasksInNew = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.NEW)
                .count();
        if (subtasksInDone == size) {
            epic.setTaskStatus(TaskStatus.DONE);
            return epic;
        } else if (subtasksInNew == size) {
            epic.setTaskStatus(TaskStatus.NEW);
            return epic;
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
            return epic;
        }
    }
}
