import org.example.threadPool.FixedThreadPool;
import org.example.threadPool.ThreadPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FixedThreadPoolTest {

    @DisplayName("Проверка, что все задачи выполняются единожды")
    @Test
    void shouldCheckThreadPoolExecution() throws InterruptedException {
        ThreadPool threadPool = new FixedThreadPool(2);
        threadPool.start();

        boolean[] tasksExecuted = {false, false, false, false, false, false};

        for (int i = 0; i < 6; i++) {
            int task = i;
            threadPool.execute(() -> {
                System.out.println("Выполнение задачи " + task + " потоком " + Thread.currentThread().getName());
                if (!tasksExecuted[task]) tasksExecuted[task] = true;
                else tasksExecuted[task] = false;
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
        FixedThreadPool threadPool = new FixedThreadPool(2);
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
