package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap;
    private final TaskLinkedList<Task> tasks;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        tasks = new TaskLinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node<Task> forRemove = historyMap.get(task.getId());
            remove(task.getId());
            removeNode(forRemove);
            tasks.linkLast(task);
            historyMap.put(task.getId(), tasks.getLastNode());
        } else {
            tasks.addFirst(task);
            historyMap.put(task.getId(), tasks.getFirstNode());
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasks.getTasks();
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }

    public void removeNode(Node<Task> node) {
        tasks.removeNode(node);
    }

    static class TaskLinkedList<T> extends LinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        private int size = 0;

        public List<T> getTasks() {
            List<T> tasks = new ArrayList<>(size);
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.getTask());
                current = current.getNext();
            }
            return tasks;
        }

        /**
         * будет добавлять задачу в конец этого списка
         */
        public void linkLast(T element) {
            final Node<T> oldTailTask = tail;
            final Node<T> newNode = new Node<>(oldTailTask, element, null);
            tail = newNode;
            if (oldTailTask == null) head = newNode;
            else oldTailTask.setNext(newNode);
            size++;
        }

        public Node<T> getLastNode() {
            final Node<T> curTail = tail;
            if (curTail == null) throw new NoSuchElementException();
            return tail;
        }

        public Node<T> getFirstNode() {
            final Node<T> curHead = head;
            if (curHead == null) throw new NoSuchElementException();
            return head;
        }

        public int size() {
            return this.size;
        }

        public void addFirst(T task) {
            final Node<T> oldHeadTask = head;
            final Node<T> newNode = new Node<>(null, task, oldHeadTask);
            head = newNode;
            if (oldHeadTask == null) tail = newNode;
            else oldHeadTask.setPrev(newNode);
            size++;
        }

        private void removeNode(Node<T> node) {
            if (node == null) {
                return;
            }

            if (node.getPrev() != null) {
                node.getPrev().setNext(node.getNext());
            } else {
                head = node.getNext();
            }

            if (node.getNext() != null) {
                node.getNext().setPrev(node.getPrev());
            } else {
                tail = node.getPrev();
            }
            size--;
        }
    }
}
