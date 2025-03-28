package server;

import client.TaskManagerHttpClient;
import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient apiClient = TaskManagerHttpClient.getClient();

    private static HttpRequest getHttpRequest(String method, String bodyJson, URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json");
        return switch (method) {
            case "GET" -> requestBuilder.GET().build();
            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(bodyJson)).build();
            case "DELETE" -> requestBuilder.DELETE().build();
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    @BeforeEach
    public void serverStart() throws IOException {
        httpTaskServer.start();
    }

    @AfterEach
    public void serverStop() {
        httpTaskServer.stop();
    }

    @Test()
    public void getAllTasks() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getTasks());
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getTasksById() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        int taskId = 3;
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getTaskById(taskId));
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getTasksByIdNotFound() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        int notExistTaskId = 888;
        URI uri = URI.create("http://localhost:8080/tasks/" + notExistTaskId);
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(404, responseCode);
    }

    @Test()
    public void createTask() {
        String taskJson = """
                {
                        "taskStatus": "NEW",
                        "name": "task name 1 ",
                        "description": "task description 1",
                        "taskType": "TASK",
                        "epicId": 999,
                        "duration": "PT3H",
                        "startTime": "2000-01-01 01:00"
                    }""";
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = getHttpRequest("POST", taskJson, uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(201, responseCode);
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test()
    public void updateTask() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        taskManager.addTask(task1);
        String taskJson = """
                {
                        "id": "1",
                        "taskStatus": "NEW",
                        "name": "updated task name 1",
                        "description": "updated task description 1",
                        "taskType": "TASK",
                        "epicId": 999,
                        "duration": "PT4H",
                        "startTime": "2000-01-01 01:00"
                    }""";
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = getHttpRequest("POST", taskJson, uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(201, responseCode);
        Task actual = taskManager.getTaskById(1);
        assertEquals("updated task description 1", actual.getDescription());
    }

    @Test()
    public void deleteTaskById() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task addedTask = taskManager.addTask(task1);
        URI uri = URI.create("http://localhost:8080/tasks/" + addedTask.getId());
        HttpRequest request = getHttpRequest("DELETE", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(200, responseCode);
        int actualSize = taskManager.getTasks().size();
        assertEquals(0, actualSize);
    }

    @Test()
    public void getAllSubtasks() {
        Epic epic = new Epic(1, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask 2 name", TaskStatus.NEW, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getSubtasks());
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getSubtasksById() {
        Epic epic = new Epic(1, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        Subtask subtask1 = new Subtask(2, TaskType.SUBTASK, "subtask 1  name", TaskStatus.NEW, "subtask 1 description", 1, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "subtask 2 name", TaskStatus.NEW, "subtask 2 description", 1, 60, "2000-01-02 02:00");
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        int subtasksId = 3;
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtasksId);
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getSubtaskById(subtasksId));
        assertEquals(expected, actualBody);
    }

    @Test()
    public void createSubtask() {
        Epic epic = new Epic(999, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        taskManager.addEpic(epic);
        String taskJson = """
                {
                        "taskStatus": "NEW",
                        "name": "task name 1 ",
                        "description": "task description 1",
                        "taskType": "SUBTASK",
                        "epicId": 999,
                        "duration": "PT3H",
                        "startTime": "2000-01-01 01:00"
                    }""";
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = getHttpRequest("POST", taskJson, uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(201, responseCode);
        List<Subtask> tasks = taskManager.getSubtasks();
        assertEquals(1, tasks.size());
    }

    @Test()
    public void updateSubtask() {
        Epic epic = new Epic(999, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(TaskType.SUBTASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        taskManager.addSubtask(subtask);
        String taskJson = """
                {
                        "id": "1",
                        "taskStatus": "NEW",
                        "name": "updated subtask name 1",
                        "description": "updated subtask description 1",
                        "taskType": "SUBTASK",
                        "epicId": 999,
                        "duration": "PT4H",
                        "startTime": "2000-01-01 01:00"
                    }""";
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = getHttpRequest("POST", taskJson, uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(201, responseCode);
        Task actual = taskManager.getSubtaskById(1);
        assertEquals("updated subtask description 1", actual.getDescription());
    }

    @Test()
    public void deleteSubtaskById() {
        Epic epic = new Epic(999, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(TaskType.SUBTASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Subtask addedTask = taskManager.addSubtask(subtask);
        URI uri = URI.create("http://localhost:8080/subtasks/" + addedTask.getId());
        HttpRequest request = getHttpRequest("DELETE", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(200, responseCode);
        int actualSize = taskManager.getSubtasks().size();
        assertEquals(0, actualSize);
    }

    @Test()
    public void getAllEpics() {
        Epic epic1 = new Epic(1, TaskType.EPIC, "Epic1 Task", TaskStatus.NEW, "Epic1 description", 0);
        Epic epic2 = new Epic(2, TaskType.EPIC, "Epic2 Task", TaskStatus.NEW, "Epic2 description", 0);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getEpics());
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getEpicsById() {
        Epic epic = new Epic(1, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        Epic addedEpic = taskManager.addEpic(epic);
        URI uri = URI.create("http://localhost:8080/epics/" + addedEpic.getId());
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getEpicById(addedEpic.getId()));
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getEpicSubtasksById() {
        Epic epic = new Epic(999, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask(TaskType.SUBTASK, "subtask name 1 ", TaskStatus.NEW, "subtask1 description 1", 999, 180, "2000-01-01 01:00");
        Subtask subtask2 = new Subtask(TaskType.SUBTASK, "subtask name 2 ", TaskStatus.NEW, "subtask 2description 1", 999, 180, "2001-01-03 01:00");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        URI uri = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(200, responseCode);
        List<Subtask> subTasksByEpicId = taskManager.getAllSubTasksByEpicId(epic.getId());
        assertEquals(2, subTasksByEpicId.size());
    }

    @Test()
    public void createEpic() {
        String taskJson = """
                {
                        "taskStatus": "NEW",
                        "name": "epic name 1 ",
                        "description": "epic description 1",
                        "taskType": "EPIC",
                        "epicId": 0
                    }""";
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = getHttpRequest("POST", taskJson, uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(201, responseCode);
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test()
    public void deleteEpicById() {
        Epic epic = new Epic(999, TaskType.EPIC, "Epic Task", TaskStatus.NEW, "Epic description", 0);
        Epic addedEpic = taskManager.addEpic(epic);
        Subtask subtask = new Subtask(TaskType.SUBTASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        taskManager.addSubtask(subtask);
        URI uri = URI.create("http://localhost:8080/epics/" + addedEpic.getId());
        HttpRequest request = getHttpRequest("DELETE", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        assertEquals(200, responseCode);
        int actualSizeSubtasks = taskManager.getSubtasks().size();
        assertEquals(0, actualSizeSubtasks);
        int actualSizeEpics = taskManager.getEpics().size();
        assertEquals(0, actualSizeEpics);
    }

    @Test()
    public void getHistory() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getHistory());
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getPrioritized() {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(taskManager.getPrioritizedTasks());
        assertEquals(expected, actualBody);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            return apiClient.send(request, handler);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            throw new RuntimeException("Во время выполнения запроса ресурса по url-адресу: " + request.uri() + " , возникла ошибка." +
                    "Проверьте, пожалуйста, адрес и повторите попытку. Текст ошибки: " + e.getMessage());
        }
    }
}