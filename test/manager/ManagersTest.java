package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager aDefault = Managers.getDefault();
        assertNotNull(aDefault.getHistory());
        assertNotNull(aDefault.getTasks());
        assertNotNull(aDefault.getSubtasks());
        assertNotNull(aDefault.getEpics());
    }

    @Test
    void getDefaultHistory() {
        HistoryManager defaultHistory = Managers.getDefaultHistory();
        assertNotNull(defaultHistory.getHistory(), "HistoryManager равен null не создался");
    }
}