package server.handler;

import manager.TaskManager;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager) {
        super(manager);
    }
}
