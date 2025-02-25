package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public InMemoryTaskManager getInMemoryTaskManager() {
        return inMemoryTaskManager;
    }

    private InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
        String taskType = split[1];
        String name = split[2];
        String status = split[3];
        String description = split[4];
        int epicId = split.length > 5 && !split[5].isEmpty() ? Integer.parseInt(split[5]) : -1;
        if (taskType.equals("TASK")) {
            return new Task(id, TaskType.valueOf(taskType), name, TaskStatus.valueOf(status), description, epicId);
        } else if (taskType.equals("SUBTASK")) {
            return new Subtask(id, TaskType.valueOf(taskType), name, TaskStatus.valueOf(status), description, epicId);
        } else if (taskType.equals("EPIC")) {
            return new Epic(id, TaskType.valueOf(taskType), name, TaskStatus.valueOf(status), description, epicId);
        }
        throw new IllegalArgumentException("Invalid task type: " + value);
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

    FileBackedTaskManager loadFromFile(File file) throws IOException {
        String allLines = Files.readString(file.toPath());
        String[] split = allLines.split("\n");
        List<String> list = new ArrayList<>(Arrays.asList(split));
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] split1 = line.split(",");
            String taskType = split1[1];
            if (taskType.equals("EPIC")) {
                Task task = fromString(line);
                inMemoryTaskManager.addEpic((Epic) task);
                iterator.remove();
            }
        }
        for(String line : list) {
            String[] split1 = line.split(",");
            String taskType = split1[1];
            if (taskType.equals("TASK")) {
                Task task = fromString(line);
                inMemoryTaskManager.addTask(task);
            }
            if (taskType.equals("SUBTASK")) {
                Task task = fromString(line);
                inMemoryTaskManager.addSubtask((Subtask) task);
            }
        }
        return this;
    }

    public int getTaskCount() throws IOException {
        String allLines = Files.readString(file.toPath());
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
