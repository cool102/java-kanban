package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import gson.adapter.DurationTypeAdapter;
import gson.adapter.LocalDateTimeTypeAdapter;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;
import task.Task;
import task.TaskStatus;
import task.TaskType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static int PORT = 8080;
    static HttpServer httpServer;
    private static TaskManager taskManager = Managers.getDefault();


    public HttpTaskServer(TaskManager manager) {
        this.taskManager = manager;
    }

    public static void main(String[] args) throws IOException {
        Task task1 = new Task(TaskType.TASK, "task name 1 ", TaskStatus.NEW, "task description 1", 999, 180, "2000-01-01 01:00");
        Task task2 = new Task(TaskType.TASK, "task name 2 ", TaskStatus.NEW, "task description 2", 999, 180, "2000-01-02 01:00");
        Task task3 = new Task(TaskType.TASK, "task name 3 ", TaskStatus.NEW, "task description 3", 999, 180, "2000-01-03 01:00");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        start();
    }

    public static void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public void stop() {
        if (httpServer != null) {
            System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
            httpServer.stop(1);
            httpServer = null;
        }
    }
}
