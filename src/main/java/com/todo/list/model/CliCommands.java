package com.todo.list.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CliCommands {
    ADD("add"),
    LIST("list"),
    EDIT("edit"),
    DELETE("delete"),
    FILTER("filter"),
    SORT("sort"),
    EXIT("exit"),
    UNRECOGNIZED("unrecognized");

    private final String lowerCaseCommand;


}
