package com.todo.list.entity;


import lombok.*;

import java.time.LocalDate;
import com.todo.list.model.TaskStatus;

@Data
@Builder

public class Task {
    private String taskName;
    private String taskDescription;
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;
    private LocalDate taskDueDate;


}
