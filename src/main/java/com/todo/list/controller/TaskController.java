package com.todo.list.controller;

import lombok.AllArgsConstructor;

import java.time.format.DateTimeParseException;
import com.todo.list.entity.Task;
import com.todo.list.model.CliCommands;
import com.todo.list.model.TaskStatus;
import com.todo.list.service.TaskService;

import java.util.Map;
import java.util.Scanner;


@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private static final String TODO_WELCOME_MESSAGE = "ToDo List";
    private static final String TODO_MENU_TEXT =
            """
                    Введите нужную команду:
                    
                    list - Показать все задачи
                    add - добавить задачу
                    edit - редактировать существующую задачу
                    delete - удалить задачу
                    filter - показать задачу с определенным статусом
                    sort - отсортировать задачи
                    exit - выход
                    """;
    private static final String ADD_TASK_TEXT =
            """
                    ДОБАВЛЕНИЕ НОВОЙ ЗАДАЧИ
                       Укажите задачу: 
                    """;


    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        CliCommands input;
        System.out.println(TODO_WELCOME_MESSAGE);
        try {
            do {
                System.out.println(TODO_MENU_TEXT);
                CliCommandParsingResult cliCommandParsingResult = getInputFromCli(scanner);
                if (!cliCommandParsingResult.successful()) {
                    input = cliCommandParsingResult.cliCommand();
                    System.out.println(cliCommandParsingResult.errorMessage);
                } else {
                    input = cliCommandParsingResult.cliCommand();
                }
                switch (input) {
                    case CliCommands.LIST -> printAllTasks();
                    case CliCommands.ADD -> addTask(scanner);
                    case CliCommands.EDIT -> {
                        System.out.println("Введите id задачи");
                        Long id;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Некорректный формат, введите число");
                            continue;
                        }
                        editTask(id, scanner);
                    }
                    case CliCommands.DELETE -> {
                        System.out.println("Введите id задачи");
                        Long id;
                        try {
                            id = Long.parseLong(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Некорректный формат, введите число");
                            continue;
                        }
                        taskService.deleteTask(id);
                    }
                    case CliCommands.FILTER -> filterTasks(scanner);
                    case CliCommands.SORT -> sortTasks(scanner);
                }
            } while (!input.equals(CliCommands.EXIT));
        } catch (Exception e) {
            System.out.println("Произошла ошибка: "+e.getMessage()+", повторите заново");
        }
    }

    private void printAllTasks() {
        Map<Long, Task> tasks = taskService.getAllTasks();
        printTasks(tasks);
    }

    private void printTasks(Map<Long, Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Задач нет");
            return;
        }
        for (Map.Entry<Long, Task> entry : tasks.entrySet()) {
            Long id = entry.getKey();
            Task task = entry.getValue();
            System.out.println(
                    id + " | " +
                            task.getTaskName() + " | " +
                            task.getTaskDescription() + " | " +
                            task.getStatus() + " | " +
                            task.getTaskDueDate()
            );
        }
    }

    private void addTask(Scanner scanner) {
        System.out.println("Введите название задачи");
        String taskName = scanner.nextLine();

        System.out.println("Введите описание задачи");
        String taskDescription = scanner.nextLine();

        System.out.println("Введите срок выполнения в формате dd.MM.yy");
        String taskDueDate = scanner.nextLine();

        try {
            taskService.addTask(taskName, taskDescription, taskDueDate);
            System.out.println("Задача добавлена");
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты. Используйте dd.MM.yy");
        }
    }


    private void editTask(Long id, Scanner scanner) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            System.out.println("Задача не найдена");
            return;
        }
        System.out.println("Укажите, что хотите изменить - Название, Описание, Статус, Срок)");
        String input = scanner.nextLine().trim().toUpperCase();
        switch (input) {
            case "НАЗВАНИЕ" -> {
                System.out.println("Введите новое название: ");
                String newTaskName = scanner.nextLine();
                taskService.editTask(id, newTaskName, null, null, null);
                System.out.println("Название обновлено");
            }
            case "ОПИСАНИЕ" -> {
                System.out.println("Введите новое описание: ");
                String newDescription = scanner.nextLine();
                taskService.editTask(id, null, newDescription, null, null);
                System.out.println("Описание обновлено");
            }
            case "СТАТУС" -> {
                System.out.println("Выберите нужный статус - todo, in progress, done");
                String newStatusStr = scanner.nextLine();
                TaskStatus newStatus = TaskStatus.fromString(newStatusStr);
                if (newStatus != null) {
                    taskService.editTask(id, null, null, newStatusStr, null);
                    System.out.println("Статус обновлен");
                } else {
                    System.out.println("Нераспознанный статус");
                }
            }
            case "СРОК" -> {
                System.out.println("Введите новый срок в формате dd.mm.yy");
                String newDateStr = scanner.nextLine();
                try {
                    taskService.editTask(id, null, null, null, newDateStr);
                    System.out.println("Срок выполнения обновлен");
                } catch (DateTimeParseException e) {
                    System.out.println("Неверный формат даты");
                }
            }
            default -> {
                System.out.println("Неверное значение");
                return;
            }
        }
    }

    private void filterTasks(Scanner scanner) {
        System.out.println("Введите фильтр - TODO, IN PROGRESS, DONE");
        String filterInput = scanner.nextLine().trim().toUpperCase();
        TaskStatus status = TaskStatus.fromString(filterInput);

        if (status==null){
            System.out.println("Нераспознанный статус");
            return;
        }
        Map<Long, Task> filteredTasks = taskService.filterTasks(status);
        printTasks(filteredTasks);
    }
    private void sortTasks(Scanner scanner){
        System.out.println("Введите сортировку - СТАТУС, СРОК или ВСЕ: ");
        String sortInput = scanner.nextLine().trim().toUpperCase();

        Map<Long, Task> sortedTasks = taskService.sortTasks(sortInput);
        printTasks(sortedTasks);
    }

    private CliCommandParsingResult getInputFromCli(Scanner scanner) {
        CliCommands command;
        try {
            String stringInput = scanner.nextLine().trim().toUpperCase();
            command = CliCommands.valueOf(stringInput);
            return new CliCommandParsingResult(command, true, null);
        } catch (IllegalArgumentException e) {
            return new CliCommandParsingResult(CliCommands.UNRECOGNIZED, false, "Нераспознанная команда");
        }
    }

    private record CliCommandParsingResult(
            CliCommands cliCommand,
            boolean successful,
            String errorMessage
    ) {

    }

    ;

}
