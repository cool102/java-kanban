package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private int taskCount = 0;

    public InMemoryTaskManager() {

    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task addTask(Task newTask) {
        overlapValidate(newTask);
        newTask.setId(generateTaskId());
        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.add(newTask);
        return newTask;
    }

    private void overlapValidate(Task newTask) {
        boolean hasOverlap = getPrioritizedTasks().stream().anyMatch(existingTask -> isOverlapping(newTask, existingTask));
        if (hasOverlap) {
            throw new RuntimeException("Ошибка: Задача " + newTask.getName() + " пересекается с уже существующей задачей.");
        }
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null || task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        LocalDateTime startTimeTask1 = task1.getStartTime();
        LocalDateTime endTimeTask2 = task2.getEndTime();
        LocalDateTime startTimeTask2 = task2.getStartTime();
        LocalDateTime endTimeTask1 = task1.getEndTime();
        boolean before = startTimeTask1.isBefore(endTimeTask2);
        boolean before1 = startTimeTask2.isBefore(endTimeTask1);
        return before && before1;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        overlapValidate(subtask);
        if (subtask.getId() == 0) {
            subtask.setId(generateTaskId());
        }
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        Integer epicId = subtask.getEpicId();
        if (epicId != null) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtasksIds().add(subtask.getId());
                epic.calculateTimeAndDuration(subtasks.values().stream().toList());
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
        if (task != null) {
            historyManager.add(task);
        }
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
        return getSubtasks().stream().filter(subtask -> subtask.getEpicId() == epicId).collect(Collectors.toList());
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
        epic.calculateTimeAndDuration(List.of(forUpdate));
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
        Task removed = tasks.remove(id);
        prioritizedTasks.remove(removed);
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
        epic.calculateTimeAndDuration(List.of(subtask));
        Subtask removed = subtasks.remove(id);
        prioritizedTasks.remove(removed);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = getEpicById(id);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        for (Integer subtasksId : subtasksIds) {
            subtasks.remove(subtasksId);
        }
        epics.remove(id);
        prioritizedTasks.remove(epic);
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
        allSubtasks.forEach(subtask -> {
            Epic epic = getEpicById(subtask.getEpicId());
            evaluateEpicStatus(epic, epic.getSubtasksIds());
        });
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

    public void evaluateEpicStatus(Epic epic, List<Integer> subtasksIds) {
        int size = subtasksIds.size();
        List<Subtask> subtasksOfEpic = subtasksIds.stream().map(subtasks::get).toList();
        if (subtasksOfEpic.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
        }
        long subtasksInDone = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.DONE).count();
        long subtasksInNew = subtasksOfEpic.stream().filter(s -> s.getTaskStatus() == TaskStatus.NEW).count();
        if (subtasksInDone == size) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (subtasksInNew == size) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
