package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.Files.readString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public String getTableHead() {
        return tableHead;
    }

    private final String tableHead;

    public FileBackedTaskManager(File file) {
        this.file = file;
        tableHead = "id,type,name,status,description,epic";
    }

    public static Task fromString(String value) {
        String[] split = value.split(",", -1);
        int id = Integer.parseInt(split[0]);
        TaskType taskType = TaskType.valueOf(split[1]);
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        int epicId = split.length > 5 && !split[5].isEmpty() ? Integer.parseInt(split[5]) : -1;
        return switch (taskType) {
            case TaskType.TASK ->
                    new Task(id, taskType, name, status, description, epicId);
            case  TaskType.SUBTASK ->
                    new Subtask(id, taskType, name, status, description, epicId);
            case  TaskType.EPIC ->
                    new Epic(id, taskType, name, status, description, epicId);
        };
    }

    public void save() {
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write(tableHead + "\n");
            List<Task> tasks = getTasks();
            List<Subtask> subtasks = getSubtasks();
            List<Epic> epics = getEpics();
            writeTaskToFile(tasks, fw);
            writeTaskToFile(subtasks, fw);
            writeTaskToFile(epics, fw);
            FileBackedTaskManager.writeTaskToFile(subtasks, fw);
        } catch (IOException ioe) {
            throw new ManagerSaveException(ioe.getMessage());
        }
    }

    private static <T extends Task> void writeTaskToFile(List<T> tasks, FileWriter fw) throws IOException {
        for (T t : tasks) {
            fw.write(t.toString());
            fw.write("\n");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        String allLines = readString(file.toPath());
        String[] split = allLines.split("\n");
        List<String> list = new ArrayList<>(Arrays.asList(split));
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith("id,")) {
                continue;
            }
            Task task = fromString(line);
            if (task instanceof Epic epic) {
                manager.addEpic(epic);
                iterator.remove();
            }
        }
        for (String line : list) {
            if (line.startsWith("id,")) {
                continue;
            }
            Task task = fromString(line);
            if (task instanceof Subtask) {
                manager.addSubtask((Subtask)task);
            } else if (task.getClass() == Task.class) {
                manager.addTask(task);
            }
        }
        return manager;
    }

    public int getTaskCount() throws IOException {
        String allLines = readString(file.toPath());
        String[] split = allLines.split("\n");
        return split.length - 1;
    }

    @Override
    public Task addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
        return updateTask;
    }

    @Override
    public Subtask updateSubtask(Subtask forUpdate) {
        super.updateSubtask(forUpdate);
        save();
        return forUpdate;
    }

    @Override
    public Epic updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
        return updateEpic;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }
}
