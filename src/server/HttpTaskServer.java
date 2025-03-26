package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import gson.LocalDateTimeTypeAdapter;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static TaskManager taskManager = Managers.getDefault();
    private static int PORT = 8080;
    static HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.taskManager = manager;
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public static void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() throws IOException {
        if (httpServer != null) {
            httpServer.stop(1);
            httpServer = null;// обнуляем, чтобы избежать повторных остановок
        }
    }
}
