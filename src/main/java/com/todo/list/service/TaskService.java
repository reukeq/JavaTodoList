package com.todo.list.service;

import lombok.AllArgsConstructor;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.todo.list.entity.Task;

import com.todo.list.model.TaskStatus;
import com.todo.list.repository.TaskRepository;

import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private DateTimeFormatter dateFormatter;

    public void addTask(String taskName, String taskDescription, String taskDueDate) {
        dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        LocalDate dueDate = LocalDate.parse(taskDueDate, dateFormatter);
        Task task = Task.builder()
                .taskName(taskName)
                .taskDescription(taskDescription)
                .taskDueDate(dueDate)
                .build();
        taskRepository.addTask(task);
    }

    public void editTask(Long id, String taskName, String taskDescription, String taskStatus, String taskDueDate) {
        Task task = getTaskById(id);
        if (taskName != null && !taskName.isBlank()) {
            task.setTaskName(taskName);
        }
        if (taskDescription != null && !taskDescription.isBlank()) {
            task.setTaskDescription(taskDescription);
        }
        if (taskStatus != null && !taskStatus.isBlank()) {
            TaskStatus parsedStatus = TaskStatus.fromString(taskStatus);
            if (parsedStatus != null) {
                task.setStatus(parsedStatus);
            } else {
                System.out.println("Нераспознанный статус + " + taskStatus);
            }
        }
        if (taskDueDate != null && !taskDueDate.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            task.setTaskDueDate(LocalDate.parse(taskDueDate, formatter));
        }
        System.out.println("Задача изменнена");
        taskRepository.editTask(id, task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        if (task != null) {
            taskRepository.deleteTask(id);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public Map<Long, Task> filterTasks(TaskStatus status) {
        return taskRepository.getAllTasks().entrySet().stream()
                .filter(entry -> entry.getValue().getStatus() == status)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public Map<Long, Task> sortTasks(String sortBy) {
        return taskRepository.getAllTasks().entrySet().stream()
                .sorted((e1, e2) -> {
                    Task t1 = e1.getValue();
                    Task t2 = e2.getValue();

                    return switch (sortBy) {
                        case "СТАТУС" -> t1.getStatus().compareTo(t2.getStatus());
                        case "СРОК" -> t1.getTaskDueDate().compareTo(t2.getTaskDueDate());
                        case "ВСЕ" -> {
                            int res = t1.getStatus().compareTo(t2.getStatus());
                            if (res == 0) {
                                res = t1.getTaskDueDate().compareTo(t2.getTaskDueDate());
                            }
                            yield res;
                        }
                        default -> 0;
                    };
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public HashMap<Long, Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public Task getTaskById(Long id) {
        return taskRepository.getTaskById(id);
    }
}
