package org.example.task;

import java.util.Scanner;

import static org.example.task.TaskRunner.task1;
import static org.example.task.TaskRunner.task2;

public class TaskHandler {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean isWorking = true;

        while (isWorking) {
            printMenu();
            int userCommand = checkIntInput(scanner);

            switch (userCommand) {
                case 1 -> {
                    task1();
                    isWorking = wishToContinue();
                }
                case 2 -> {
                    task2();
                    isWorking = wishToContinue();
                }
                case 0 -> {
                    System.out.println("До свидания! Завершение программы...");
                    isWorking = false;
                }
                default -> System.out.println("Извините, такая команда отсутствует :с");
            }
        }
    }

    private void printMenu() {
        System.out.println(
                """
                        Задание №11 - ThreadPool
                        Пожалуйста, выберите одну из цифр (0-2) для выбора проверки задания:
                        1 - Проверка работы FixedThreadPool
                        2 - Проверка работы ScalableThreadPool
                        0 - Выход
                                                
                        Ввод:"""
        );
    }

    private int checkIntInput(Scanner scanner) {
        int num;
        do {
            if (scanner.hasNextInt()) {
                num = scanner.nextInt();
                break;
            } else {
                System.out.print("Не могу распознать число. Введите числовое значение: ");
                scanner.nextLine();
            }
        } while (true);
        return num;
    }

    private boolean wishToContinue() {
        System.out.println("\nЖелаете продолжить?\n1 - Вывести меню\n0 - Выйти");
        int answer;
        while (true) {
            System.out.println("Ввод: ");
            answer = checkIntInput(new Scanner(System.in));
            switch (answer) {
                case 1 -> {
                    return true;
                }
                case 0 -> {
                    return false;
                }

                default -> System.out.println("Извините, такая команда отсутствует :с");
            }
        }
    }
}
