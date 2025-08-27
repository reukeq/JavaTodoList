package com.todo.list.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in progress"),
    DONE("done");

    private final String statusName;

    public static TaskStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String status = value.trim().toUpperCase().replace(" ", "_");
        try {
            return TaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
