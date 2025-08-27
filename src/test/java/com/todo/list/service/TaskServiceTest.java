package com.todo.list.service;

import com.todo.list.entity.Task;
import com.todo.list.model.TaskStatus;
import com.todo.list.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private Task testTask;
    private final Long testId = 1L;

    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .taskName("Test Task")
                .taskDescription("Test Description")
                .taskDueDate(LocalDate.of(2023, 12, 31))
                .build();
    }


    @Test
    void addTask_addCorrectTask() {
        String taskName = "New Task";
        String taskDescription = "New Description";
        String taskDueDate = "31.12.23";

        taskService.addTask(taskName, taskDescription, taskDueDate);

        verify(taskRepository).addTask(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskName, capturedTask.getTaskName());
        assertEquals(taskDescription, capturedTask.getTaskDescription());
        assertEquals(LocalDate.of(2023, 12, 31), capturedTask.getTaskDueDate());
    }


    @Test
    void editTask_updateTaskAll() {
        when(taskRepository.getTaskById(testId)).thenReturn(testTask);
        String newName = "Updated Task";
        String newDescription = "Updated Description";
        String newStatus = "In progress";
        String newDueDate = "01.01.24";

        taskService.editTask(testId, newName, newDescription, newStatus, newDueDate);

        verify(taskRepository).editTask(eq(testId), taskCaptor.capture());
        Task updatedTask = taskCaptor.getValue();
        assertEquals(newName, updatedTask.getTaskName());
        assertEquals(newDescription, updatedTask.getTaskDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(LocalDate.of(2024, 1, 1), updatedTask.getTaskDueDate());
    }

    @Test
    void editTask_InvalidStatusIgnored() {
        when(taskRepository.getTaskById(testId)).thenReturn(testTask);
        String invalidStatus = "INVALID_STATUS";

        taskService.editTask(testId, null, null, invalidStatus, null);

        verify(taskRepository).editTask(eq(testId), taskCaptor.capture());
        Task updatedTask = taskCaptor.getValue();
        assertEquals(TaskStatus.TODO, updatedTask.getStatus()); // статус не изменился
    }

    @Test
    void deleteTask_removeTask() {
        when(taskRepository.getTaskById(testId)).thenReturn(testTask);
        taskService.deleteTask(testId);
        verify(taskRepository).deleteTask(testId);
    }

    @Test
    void deleteTask_TaskNotFound_DoesNothing() {
        when(taskRepository.getTaskById(testId)).thenReturn(null);
        taskService.deleteTask(testId);
        verify(taskRepository, never()).deleteTask(anyLong());
    }

    @Test
    void filterAndSortTasks() {
        HashMap<Long, Task> allTasks = new HashMap<>();
        Task task1 = Task.builder().status(TaskStatus.TODO).taskDueDate(LocalDate.of(2023, 12, 31)).build();
        Task task2 = Task.builder().status(TaskStatus.IN_PROGRESS).taskDueDate(LocalDate.of(2023, 1, 1)).build();
        Task task3 = Task.builder().status(TaskStatus.DONE).taskDueDate(LocalDate.of(2023, 6, 15)).build();

        allTasks.put(1L, task1);
        allTasks.put(2L, task2);
        allTasks.put(3L, task3);

        when(taskRepository.getAllTasks()).thenReturn(allTasks);

        Map<Long, Task> filtered = taskService.filterTasks(TaskStatus.IN_PROGRESS);
        assertEquals(1, filtered.size());
        assertTrue(filtered.containsValue(task2));

        Map<Long, Task> sorted = taskService.sortTasks("ВСЕ");
        var values = sorted.values().stream().toList();
        assertEquals(TaskStatus.TODO, values.get(0).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, values.get(1).getStatus());
        assertEquals(TaskStatus.DONE, values.get(2).getStatus());
    }
}
