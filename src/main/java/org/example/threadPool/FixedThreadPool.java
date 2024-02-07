package org.example.threadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.String.format;

public class FixedThreadPool implements ThreadPool {
    private final List<ThreadWorker> threads;
    private final Queue<Runnable> taskQueue;
    private volatile boolean isRunning = true;

    public FixedThreadPool(int poolSize) {
        if (poolSize <= 0) throw new IllegalArgumentException(
                format("Переданное в конструктор значение %d не является положительным!", poolSize));
        threads = new ArrayList<>(poolSize);
        taskQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < poolSize; i++) {
            threads.add(new ThreadWorker());
        }
    }

    @Override
    public void start() {
        threads.forEach(ThreadWorker::start);
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (taskQueue) {
            taskQueue.offer(runnable);
            taskQueue.notify();
        }
    }

    @Override
    public void interrupt() {
        isRunning = false;
        threads.forEach(Thread::interrupt);
    }

    public boolean areAllThreadsTerminated() {
        for (ThreadWorker thread : threads) {
            if (thread.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private final class ThreadWorker extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                final Runnable task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && isRunning) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException exception) {
                            if (!isRunning) {
                                return;
                            }
                        }
                    }
                    task = taskQueue.poll();
                }
                try {
                    assert task != null;
                    task.run();
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
