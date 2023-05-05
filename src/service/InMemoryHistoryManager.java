package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager extends Node implements HistoryManager{
    private static class CustomLinkedList {
        private final Map<Integer, Node> map = new HashMap<>();
        private Node head;
        private Node tail;

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
                if(next != null) {
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
                node.setNext(null);
                node.setPrev(null);
            } else {
                node.setNext(null);
                node.setPrev(tail);
                tail.setNext(node);
                tail = node;
            }
            map.put(task.getId(), node);
        }

        private List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            Node node = head;
            while (node != null) {
                result.add(node.getData());
                node = node.getNext();
            }
            return result;
        }

        public Node getNode(int id) {
            return map.get(id);
        }
    }
CustomLinkedList list = new CustomLinkedList();

    @Override
    public void add(Task task) {
        list.linkLast(task);
    }

    @Override
    public Node get(int id){
       return list.getNode(id);
    }

    @Override
    public void remove(int id) {
        list.removeNode(list.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return list.getTasks();

    }
}

