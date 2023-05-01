package service;

import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager extends Node implements HistoryManager {
    private static class CustomLinkedList {
        private final Map<Integer, Node> map = new HashMap<>();
        private Node head;
        private Node tail;

        private List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            Node node = head;
            while (node != null) {
                result.add(node.getData());
                node = node.getNext();
            }
            return result;
        }
        private Node getNode(int id) {
            return map.get(id);
        }
        private void removeNode(Node node) {
            if (node != null) {
                map.remove(node.getData().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();
                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }
                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }
        private void linkLast(Task task) {
            Node node = new Node();
            node.setData(task);
            if (map.containsKey(task.getId())) {
                removeNode(map.get(task.getId()));
            }
            if (head == null) {
                tail = node;
                head = node;
            } else {
                node.setPrev(tail);
                tail.setNext(node);
                tail = node;
            }
            map.put(task.getId(), node);
        }
    }
    private final CustomLinkedList list = new CustomLinkedList();

    @Override
    public void add(Task task) {
        list.linkLast(task);
    }

    @Override
    public void remove(SubTask id) {
        list.removeNode(list.getNode(id.getId()));
    }

    @Override
    public List<Task> getHistory() {
        return list.getTasks();
    }


}