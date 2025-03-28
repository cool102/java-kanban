package server.handler;

import manager.TaskManager;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }
}
