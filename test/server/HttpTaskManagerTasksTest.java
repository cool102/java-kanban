package server;

import client.TaskManagerHttpClient;
import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    HttpClient apiClient = TaskManagerHttpClient.getClient();

    @BeforeEach
    public void serverStart() throws IOException, InterruptedException {
        httpTaskServer.start();
    }

    @AfterEach
    public void serverStop() throws IOException {
        httpTaskServer.stop();
    }

    @Test()
    public void getAllTasks() throws InterruptedException {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(manager.getTasks());
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getTasksById() throws InterruptedException {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        int taskId = 3;
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(200, responseCode);
        String expected = gson.toJson(manager.getTaskById(taskId));
        assertEquals(expected, actualBody);
    }

    @Test()
    public void getTasksByIdNotFound() throws InterruptedException {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        int notExistTaskId = 888;
        URI uri = URI.create("http://localhost:8080/tasks/" + notExistTaskId);
        HttpRequest request = getHttpRequest("GET", "", uri);
        HttpResponse<String> response = sendRequest(request);
        int responseCode = response.statusCode();
        String actualBody = response.body();

        assertEquals(404, responseCode);
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

    private static HttpRequest getHttpRequest(String method, String bodyJson, URI uri) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json");
        switch (method) {
            case "GET":
                return requestBuilder.GET().build();
            case "POST":
                return requestBuilder.POST(HttpRequest.BodyPublishers.ofString(bodyJson)).build();
                case "DELETE":requestBuilder.DELETE().build();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
