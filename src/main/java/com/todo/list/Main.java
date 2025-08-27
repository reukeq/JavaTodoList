package com.todo.list;

import com.todo.list.controller.TaskController;
import com.todo.list.repository.TaskRepository;
import com.todo.list.service.TaskService;

public class Main {
    public static void main(String[] args) {
        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(taskRepository,null);
        TaskController taskController = new TaskController(taskService);

        taskController.runApp();
    }
}
