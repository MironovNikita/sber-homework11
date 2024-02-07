package org.example.threadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.String.format;

public class ScalableThreadPool implements ThreadPool {
    private final int minPoolSize;
    private final int maxPoolSize;
    private final List<WorkerThread> threads;
    private final Queue<Runnable> taskQueue;
    private volatile boolean isRunning = true;

    public ScalableThreadPool(int minPoolSize, int maxPoolSize) {
        if (minPoolSize <= 0 || maxPoolSize <= 0 || minPoolSize >= maxPoolSize) {
            throw new IllegalArgumentException(
                    format("Проверьте переданные в конструктор значения: min: %d, max: %d. Они должны быть" +
                                    "положительными и не равны друг другу!",
                            minPoolSize, maxPoolSize));
        }

        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        threads = new ArrayList<>();
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void start() {
        for (int i = 0; i < minPoolSize; i++) {
            addWorker();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (taskQueue) {
            taskQueue.offer(runnable);
            if (threads.size() < maxPoolSize && taskQueue.size() > threads.size()) {
                addWorker();
            }
            taskQueue.notify();
        }
    }

    @Override
    public void interrupt() {
        isRunning = false;
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }
        synchronized (threads) {
            threads.forEach(Thread::interrupt);
        }
    }

    private void addWorker() {
        WorkerThread workerThread = new WorkerThread();
        threads.add(workerThread);
        workerThread.start();
    }

    public boolean areAllThreadsTerminated() {
        for (WorkerThread thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private final class WorkerThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                final Runnable task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && isRunning) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException exception) {
                            return;
                        }
                    }
                    task = taskQueue.poll();
                }
                if (task != null) {
                    try {
                        task.run();
                    } catch (RuntimeException exception) {
                        exception.printStackTrace();
                    }
                }
                synchronized (taskQueue) {
                    if (taskQueue.isEmpty() && threads.size() > minPoolSize) {
                        threads.remove(this);
                        return;
                    }
                }
            }
        }
    }
}
