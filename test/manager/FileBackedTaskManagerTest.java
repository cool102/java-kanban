package manager;

import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    File savedFile = new File("savedTasks");

    @Test
    public void taskToStringTest() {
        Task task = new Task(1, TaskType.TASK, "task name",TaskStatus.NEW, "description", 2 );
        System.out.println(task);
    }

    @Test
    public void createNewTaskFromString() throws IOException {
        String taskString = "1,TASK,task name,NEW,description,2";
        Task task = FileBackedTaskManager.fromString(taskString);
        assertEquals(1,task.getId());
        assertEquals("task name",task.getName());
    }

    @Test
    public void addTaskToFileTest() throws IOException {
        File file = File.createTempFile("fileBackedTaskManager","temp");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String taskString1 = "1,TASK,task name,NEW,description,2";
        String taskString2 = "11,SUBTASK,subtask name,NEW,description,3";
        Task task1 = FileBackedTaskManager.fromString(taskString1);
        Task task2 = FileBackedTaskManager.fromString(taskString2);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        int actual = fileBackedTaskManager.getTaskCount();
        assertEquals(2, actual, "task count in file not eqaul created task quantity");
    }

    @Test
    public void saveAndLoadEmptyFileTest() throws IOException {
        File file = File.createTempFile("fileBackedTaskManager","temp");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.save();
        String firstLine = Files.readString(file.toPath()).replace("\n", "");
        assertEquals(firstLine, fileBackedTaskManager.getTableHead());
    }

    @Test
    public void loadFromFileToMemory() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(savedFile);
        String taskString1 = "7,EPIC,subtask name,NEW,description,";
        String taskString2 = "9,TASK,task name,NEW,description,";
        String taskString3 = "36,SUBTASK,subtask name,NEW,description,7";
        Task epic = FileBackedTaskManager.fromString(taskString1);
        Task task = FileBackedTaskManager.fromString(taskString2);
        Task subtask = FileBackedTaskManager.fromString(taskString3);
        fileBackedTaskManager.addTask(epic);
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(subtask);

        fileBackedTaskManager.loadFromFile(savedFile);
        int taskCount = fileBackedTaskManager.getInMemoryTaskManager().getTasks().size();
        int subtaskCount = fileBackedTaskManager.getInMemoryTaskManager().getSubtasks().size();
        int epicCount = fileBackedTaskManager.getInMemoryTaskManager().getEpics().size();
        assertEquals(1, taskCount, "task count in memory not equal loaded task quantity");
        assertEquals(1, subtaskCount, "subtask count in memory not equal loaded subtask quantity");
        assertEquals(1, epicCount, "epic count in memory not equal loaded epics quantity");
    }

    @Test
    public void updateTaskSaveToFileTest() throws IOException {
        File file = File.createTempFile("fileBackedTaskManager","temp");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String oldTaskName = "task name";
        String taskLine = "1,TASK," + oldTaskName + ",NEW,description,2";
        Task task = FileBackedTaskManager.fromString(taskLine);
        fileBackedTaskManager.addTask(task);
        String newTaskName = "task name 2";
        task.setName(newTaskName);
        fileBackedTaskManager.updateTask(task);
        String allLines = Files.readString(file.toPath());
        String[] splited = allLines.split("\n");
        String modifiedTask = splited[1];
        String[] splittedTask = modifiedTask.split(",");
        String actual = splittedTask[2];
        assertEquals(actual, newTaskName);
    }
}
