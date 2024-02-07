package org.example;

import org.example.task.TaskHandler;

public class Main {
    public static void main(String[] args) {
        TaskHandler taskHandler = new TaskHandler();
        taskHandler.run();
    }
}