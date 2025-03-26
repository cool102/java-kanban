package server;

import client.TaskManagerHttpClient;
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

public class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    HttpClient apiClient = TaskManagerHttpClient.getClient();

    @BeforeEach
    public void serverStart() throws IOException, InterruptedException {
        httpTaskServer.start();
    }

    @AfterEach
    public void serverStop() throws IOException {
        httpTaskServer.stop();
    }

    @Test
    public void startServer() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void getTasks() throws InterruptedException {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
                .header("Accept", "application/json")
                .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = null;
        try {
            response = apiClient.send(request, handler);
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + uri + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            e.printStackTrace();
        }
    }
}
