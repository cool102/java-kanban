package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.endpoints.Endpoint;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager;

    public BaseHttpHandler(TaskManager manager) {
        this.taskManager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream requestBody = httpExchange.getRequestBody();
        Headers requestHeaders = httpExchange.getRequestHeaders();// Тело запроса
        URI uri = httpExchange.getRequestURI();
        Endpoint endpoint = getEndpoint(httpExchange);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        switch (endpoint) {
            case GET_TASKS:
                System.out.println();
                List<Task> tasks = taskManager.getTasks();
                String json = gson.toJson(tasks);
                sendText(httpExchange, json);
                break;
            case GET_TASK_BY_ID:
                sendText(httpExchange, taskManager.getTaskById(Integer.parseInt(uri.getPath().split("/")[3])).toString());
                break;
            case CREATE_TASK:
                //...
                break;
            case UPDATE_TASK:
                //...
                break;
            case DELETE_TASK:
                //...
                break;
            case GET_SUBTASKS:
                sendText(httpExchange, taskManager.getSubtasks().toString());
                break;
            case GET_SUBTASK_BY_ID:
                sendText(httpExchange, taskManager.getSubtaskById(Integer.parseInt(uri.getPath().split("/")[3])).toString());
                break;
            case CREATE_SUBTASK:
                //...
                break;
            case UPDATE_SUBTASK:
                //...
                break;
        }

        // отправляем тело ответа, записывая строку в выходящий поток
        String response = "Hey! Glad to see you on our server.";
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void sendText(HttpExchange httpExchange, String text) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(200, resp.length);
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

    private Endpoint getEndpoint(HttpExchange httpExchange) {
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();           // Напр., "/tasks/123"
        String[] split = path.split("/");             // ["", "tasks", "123"]
        int length = split.length;
        String method = httpExchange.getRequestMethod(); // "GET", "POST", "DELETE"...

        // =============== TASKS ===============
        // /tasks
        if (length == 2 && "tasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_TASKS;
            } else if ("POST".equals(method)) {
                return Endpoint.CREATE_TASK;
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
            } else if ("POST".equals(method)) {
                return Endpoint.CREATE_SUBTASK;
            }
        }
        // /subtasks/{id}
        if (length == 3 && "subtasks".equals(split[1])) {
            if ("GET".equals(method)) {
                return Endpoint.GET_SUBTASK_BY_ID;
            } else if ("POST".equals(method)) {
                return Endpoint.UPDATE_SUBTASK;       // Update через POST /subtasks/{id}
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
            // Если нужен Update Epic через POST /epics/{id},
            // добавьте в enum и в ветку if ("POST".equals(method))
            // return Endpoint.UPDATE_EPIC;
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

        // Ничего не подошло — можно вернуть null или специальный UNKNOWN
        return Endpoint.UNKNOWN;
    }
}
