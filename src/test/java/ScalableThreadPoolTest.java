import org.example.threadPool.FixedThreadPool;
import org.example.threadPool.ScalableThreadPool;
import org.example.threadPool.ThreadPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScalableThreadPoolTest {
    @DisplayName("Проверка, что все задачи выполняются единожды")
    @Test
    void shouldCheckThreadPoolExecution() throws InterruptedException {
        ThreadPool threadPool = new ScalableThreadPool(2, 4);
        threadPool.start();

        boolean[] tasksExecuted = {false, false, false, false, false, false};

        for (int i = 0; i < 6; i++) {
            int task = i;
            threadPool.execute(() -> {
                System.out.println("Выполнение задачи " + task + " потоком " + Thread.currentThread().getName());
                tasksExecuted[task] = !tasksExecuted[task];
            });
        }

        Thread.sleep(1000);

        threadPool.interrupt();

        Thread.sleep(1000);

        assertTrue(tasksExecuted[0]);
        assertTrue(tasksExecuted[1]);
        assertTrue(tasksExecuted[2]);
        assertTrue(tasksExecuted[3]);
        assertTrue(tasksExecuted[4]);
        assertTrue(tasksExecuted[5]);
    }

    @DisplayName("Проверка, что все потоки прерываются после выполнения своих задач")
    @Test
    void shouldCheckThreadPoolIsInterrupted() throws InterruptedException {
        ScalableThreadPool threadPool = new ScalableThreadPool(2, 4);
        threadPool.start();

        for (int i = 0; i < 5; i++) {
            threadPool.execute(() -> {
                System.out.println("Работает поток " + Thread.currentThread().getName());
            });
        }

        Thread.sleep(1000);

        threadPool.interrupt();

        Thread.sleep(1000);

        assertTrue(threadPool.areAllThreadsTerminated(), "Все потоки в пуле должны быть прерваны");
    }

}