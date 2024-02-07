package org.example.task;

import org.example.threadPool.FixedThreadPool;
import org.example.threadPool.ScalableThreadPool;
import org.example.threadPool.ThreadPool;
import org.example.utils.Counter;

public class TaskRunner {
    public static void task1() {
        System.out.println("Выбрано: Проверка работы FixedThreadPool");

        Counter counter = new Counter();
        ThreadPool fixedThreadPool = new FixedThreadPool(3);
        fixedThreadPool.start();

        for (int i = 1; i <= 5; i++) {
            int threadNumber = i;

            fixedThreadPool.execute(() -> {
                System.out.println("\nЗадача " + threadNumber + " выполняется потоком " +
                        Thread.currentThread().getName());
                double answer = 0;
                answer += counter.count(threadNumber);
                System.out.println("\nОтвет потока " + Thread.currentThread().getName()
                        + " для значения " + threadNumber + " равен " + answer);
            });
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fixedThreadPool.interrupt();
    }

    public static void task2() {
        System.out.println("Выбрано: Проверка работы ScalableThreadPool");

        Counter counter = new Counter();
        ThreadPool scalableThreadPool = new ScalableThreadPool(2, 5);
        scalableThreadPool.start();

        for (int i = 1; i <= 10; i++) {
            int threadNumber = i;

            scalableThreadPool.execute(() -> {
                System.out.println("\nЗадача " + threadNumber + " выполняется потоком " +
                        Thread.currentThread().getName());
                double answer = 0;
                answer += counter.count(threadNumber);
                System.out.println("\nОтвет потока " + Thread.currentThread().getName() + " равен " + answer);
            });
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scalableThreadPool.interrupt();
    }
}