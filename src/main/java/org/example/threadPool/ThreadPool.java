package org.example.threadPool;

public interface ThreadPool {
    /**
     * Starts threads. Threads are idle, until a new job appears in the queue (see execute).
     */
    void start();

    /**
     * Puts this task in a queue. The freed thread must complete this task. Each task must be completed exactly 1 time.
     */
    void execute(Runnable runnable);

    /**
     * Is used to interrupt the execution thread.
     */
    void interrupt();
}
