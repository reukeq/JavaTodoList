package com.todo.list.repository;


import com.todo.list.entity.Task;
import java.util.HashMap;
import java.util.Map;


public class TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();
    private long lastId = 0;

    public void addTask(Task task) {
        Long id = generateId();
        tasks.put(id, task);
    }

    public void editTask(Long id, Task task) {
        tasks.put(id, task);
    }

    public void deleteTask(Long id) {
        tasks.remove(id);
    }

    public Long generateId() {
        lastId += 1;
        return lastId;
    }

    public HashMap<Long, Task> getAllTasks() {
        return new HashMap<>(tasks);
    }

    public Task getTaskById(Long id) {
        return tasks.get(id);
    }
}
