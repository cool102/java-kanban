package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gson.adapter.DurationTypeAdapter;
import gson.adapter.LocalDateTimeTypeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import server.endpoints.Endpoint;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = (InMemoryTaskManager) taskManager;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        return gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream requestBody = httpExchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        URI uri = httpExchange.getRequestURI();
        Endpoint endpoint = getEndpoint(httpExchange, body);
        switch (endpoint) {
            case GET_TASKS:
                List<Task> tasks = taskManager.getTasks();
                String json = getGson().toJson(tasks);
                sendText(httpExchange, json);
                break;
            case GET_TASK_BY_ID:
                int id = Integer.parseInt(uri.getPath().split("/")[2]);
                Task taskById = taskManager.getTaskById(id);
                if (taskById != null) {
                    sendText(httpExchange, getGson().toJson(taskById));
                } else {
                    sendNoFound(httpExchange, "");
                }
                break;
            case CREATE_TASK:
                Task createTask = getGson().fromJson(body, Task.class);
                try {
                    taskManager.addTask(createTask);
                    sendText(httpExchange, "");
                } catch (RuntimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
                break;
            case UPDATE_TASK:
                Task updatedTask = getGson().fromJson(body, Task.class);
                try {
                    taskManager.updateTask(updatedTask);
                    sendText(httpExchange, "");
                } catch (RuntimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
                break;
            case DELETE_TASK:
                int idForRemove = Integer.parseInt(uri.getPath().split("/")[2]);
                taskManager.removeTask(idForRemove);
                sendText(httpExchange, "");
                break;
            case GET_SUBTASKS:
                List<Subtask> subtasks = taskManager.getSubtasks();
                String jsonSubtasks = getGson().toJson(subtasks);
                sendText(httpExchange, jsonSubtasks);
                break;
            case GET_SUBTASK_BY_ID:
                int subtaskId = Integer.parseInt(uri.getPath().split("/")[2]);
                Subtask subtaskById = taskManager.getSubtaskById(subtaskId);
                if (subtaskById != null) {
                    sendText(httpExchange, getGson().toJson(subtaskById));
                } else {
                    sendNoFound(httpExchange, "");
                }
                break;
            case CREATE_SUBTASK:
                Subtask createdSubtask = getGson().fromJson(body, Subtask.class);
                try {
                    taskManager.addSubtask(createdSubtask);
                    sendText(httpExchange, "");
                } catch (RuntimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
                break;
            case UPDATE_SUBTASK:
                Subtask updatedSubtask = getGson().fromJson(body, Subtask.class);
                try {
                    taskManager.updateSubtask(updatedSubtask);
                    sendText(httpExchange, "");
                } catch (RuntimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
                break;
            case DELETE_SUBTASK:
                int subIdForRemove = Integer.parseInt(uri.getPath().split("/")[2]);
                taskManager.removeSubtask(subIdForRemove);
                sendText(httpExchange, "");
                break;
            case GET_EPICS:
                List<Epic> epics = taskManager.getEpics();
                String jsonEpics = getGson().toJson(epics);
                sendText(httpExchange, jsonEpics);
                break;
            case GET_EPIC_BY_ID:
                int epicId = Integer.parseInt(uri.getPath().split("/")[2]);
                Epic epicById = taskManager.getEpicById(epicId);
                if (epicById != null) {
                    sendText(httpExchange, getGson().toJson(epicById));
                } else {
                    sendNoFound(httpExchange, "");
                }
                break;
            case GET_EPIC_SUBTASKS_BY_ID:
                int epicIdForSubtasks = Integer.parseInt(uri.getPath().split("/")[2]);
                List<Subtask> subTasksByEpicId = taskManager.getAllSubTasksByEpicId(epicIdForSubtasks);
                String jsonSubTasksByEpicId = getGson().toJson(subTasksByEpicId);
                sendText(httpExchange, jsonSubTasksByEpicId);
                break;
            case CREATE_EPIC:
                Epic createEpic = getGson().fromJson(body, Epic.class);
                taskManager.addEpic(createEpic);
                sendText(httpExchange, "");
                break;
            case DELETE_EPIC:
                int epicIdForRemove = Integer.parseInt(uri.getPath().split("/")[2]);
                taskManager.removeEpic(epicIdForRemove);
                sendText(httpExchange, "");
                break;
            case GET_HISTORY:
                List<Task> tasksInHistory = taskManager.getHistory();
                String jsonTasksInHistory = getGson().toJson(tasksInHistory);
                sendText(httpExchange, jsonTasksInHistory);
                break;
            case GET_PRIORITIZED_TASKS:
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String jsonPrioritizedTasks = getGson().toJson(prioritizedTasks);
                sendText(httpExchange, jsonPrioritizedTasks);
                break;
        }
    }

    public void sendText(HttpExchange httpExchange, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            String requestMethod = httpExchange.getRequestMethod();
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            if ("POST".equalsIgnoreCase(requestMethod)) {
                httpExchange.sendResponseHeaders(201, resp.length);
            } else {
                httpExchange.sendResponseHeaders(200, resp.length);
            }
            httpExchange.getResponseBody().write(resp);
            httpExchange.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNoFound(HttpExchange httpExchange, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(404, resp.length);
            httpExchange.getResponseBody().write(resp);
            httpExchange.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHasInteractions(HttpExchange httpExchange, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(406, resp.length);
            httpExchange.getResponseBody().write(resp);
            httpExchange.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Endpoint getEndpoint(HttpExchange httpExchange, String body) {
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] split = path.split("/");
        int length = split.length;
        String method = httpExchange.getRequestMethod();

        // =============== TASKS ===============
        // /tasks
        if (length == 2 && "tasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_TASKS;
            } else {
                if ("POST".equals(method) && body.contains("id")) {
                    return Endpoint.UPDATE_TASK;
                } else {
                    return Endpoint.CREATE_TASK;
                }
            }
        }
        // /tasks/{id}
        if (length == 3 && "tasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_TASK_BY_ID;
            } else if ("POST".equals(method)) {
                return Endpoint.UPDATE_TASK;         // Update через POST /tasks/{id}
            } else if ("DELETE".equals(method)) {
                return Endpoint.DELETE_TASK;
            }
        }

        // =============== SUBTASKS ===============
        // /subtasks
        if (length == 2 && "subtasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_SUBTASKS;
            } else {
                if ("POST".equals(method) && body.contains("id")) {
                    return Endpoint.UPDATE_SUBTASK;
                } else {
                    return Endpoint.CREATE_SUBTASK;
                }
            }
        }
        // /subtasks/{id}
        if (length == 3 && "subtasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_SUBTASK_BY_ID;
            } else if ("DELETE".equals(method)) {
                return Endpoint.DELETE_SUBTASK;
            }
        }

        // =============== EPICS ===============
        // /epics
        if (length == 2 && "epics".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_EPICS;
            } else if ("POST".equals(method)) {
                return Endpoint.CREATE_EPIC;
            }
        }
        // /epics/{id}
        if (length == 3 && "epics".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_EPIC_BY_ID;
            } else if ("DELETE".equals(method)) {
                return Endpoint.DELETE_EPIC;
            }

        }
        // /epics/{id}/subtasks
        if (length == 4 && "epics".equals(split[1]) && "subtasks".equals(split[3])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_EPIC_SUBTASKS_BY_ID;
            }
        }

        // =============== HISTORY ===============
        // /history
        if (length == 2 && "history".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_HISTORY;
            }
        }

        // =============== PRIORITIZED ===============
        // /prioritized
        if (length == 2 && "prioritized".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_PRIORITIZED_TASKS;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
