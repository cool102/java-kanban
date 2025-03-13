package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private final File savedFile = new File("savedTasks");
    private File file = new File("savedTasks");

    @BeforeEach
    public void setup() throws IOException {
       file = File.createTempFile("fileBackedTaskManager","temp");
    }
    @Test
    public void taskToStringTest() {
        Task task = new Task(1, TaskType.TASK, "task name",TaskStatus.NEW, "description", 2, 120, "2000-01-01 04:00" );
        System.out.println(task);
    }

    @Test
    public void createNewTaskFromString()  {
        String taskString = "1,TASK,task name,NEW,task description,999,180,2000-01-01 01:00,2000-01-01 04:00";
        Task task = FileBackedTaskManager.fromString(taskString);
        assertEquals(1,task.getId());
        assertEquals("task name",task.getName());
    }

    @Test
    public void addTaskToFileTest() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String taskString1 = "1,TASK,task name,NEW,task description,999,180,2000-01-01 01:00,2000-01-01 04:00";
        String taskString2 = "11,TASK,task name,NEW,task description,3,180,2000-01-02 01:00,2000-01-02 04:00";
        Task task1 = FileBackedTaskManager.fromString(taskString1);
        Task task2 = FileBackedTaskManager.fromString(taskString2);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        int actual = fileBackedTaskManager.getTaskCount();
        assertEquals(2, actual, "task count in file not equal created task quantity");
    }

    @Test
    public void saveAndLoadEmptyFileTest() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.save();
        String firstLine = Files.readString(file.toPath()).replace("\n", "");
        assertEquals(firstLine, fileBackedTaskManager.getTableHead());
    }

    @Test
    public void loadFromFileToMemory() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(savedFile);
        int taskCount = fileBackedTaskManager.getTasks().size();
        int subtaskCount = fileBackedTaskManager.getSubtasks().size();
        int epicCount = fileBackedTaskManager.getEpics().size();
        assertEquals(1, taskCount, "task count in memory not equal loaded task quantity");
        assertEquals(1, subtaskCount, "subtask count in memory not equal loaded subtask quantity");
        assertEquals(1, epicCount, "epic count in memory not equal loaded epics quantity");
    }

    @Test
    public void updateTaskSaveToFileTest() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        String taskLine = "1,TASK,task name,NEW,task description,2,180,2000-01-01 01:00,2000-01-01 04:00";
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
