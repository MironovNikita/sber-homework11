
# ThreadPool

### ✨Немного теории

🛠️ **ThreadPool** - это механизм, который предоставляет пул потоков для выполнения асинхронных задач. Он позволяет эффективно управлять потоками в приложении, особенно когда требуется выполнение большого количества задач.

Основные компоненты **ThreadPool** включают:

1. **`Executor`**. Этот интерфейс представляет собой механизм для выполнения задач. Он определяет методы для отправки задач на выполнение.

2. **`ExecutorService`**. Это подинтерфейс **`Executor`**, который предоставляет более высокоуровневые методы для управления выполнением задач и получения результатов выполнения.

3. **`ThreadPoolExecutor`**. Это реализация интерфейса **`ExecutorService`**. Он предоставляет гибкий механизм управления пулом потоков, включая настройку размера пула, очереди задач, обработки ошибок и другие параметры.

**Коротенький пример**:
```java
public class Main {
    public static void main(String[] args) {
        // Создание пула потоков с фиксированным размером
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Подача задач на выполнение
        for (int i = 0; i < 10; i++) {
            Runnable task = new Task(i);
            executor.execute(task);
        }

        // Завершение работы пула потоков после выполнения задач
        executor.shutdown();
    }
}

class Task implements Runnable {
    private int taskId;

    public Task(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {
        System.out.printf("Задача %d выполняется потоком: %s", taskId, Thread.currentThread().getName());
    }
}
```
В этом примере создаётся пул потоков с фиксированным размером (5 потоков), и 10 задач подаются на выполнение. Каждая задача выводит информацию о своём выполнении определённым потоком.

**`ThreadPool`** помогает управлять ресурсами системы и повышает производительность приложения за счёт повторного использования потоков и уменьшения накладных расходов на создание новых потоков.

### 🚀Практика

В данной работе представлена реализация 2 заданий, связанных с вышеописанной темой:
1. Реализация **`FixedThreadPool`** - количество потоков задается в конструкторе и не меняется.
2. Реализация **`ScalableThreadPool`** - в конструкторе задается минимальное и максимальное(int min, int max) число потоков.
## Задание

Реализовать **`ThreadPool`**
```java
public interface ThreadPool {
    void start(); // Запускает потоки. Потоки бездействуют до тех пор, пока не появится новое задание в очереди (см. execute)

    void execute(Runnable runnable); // Складывает это задание в очередь. Освободившийся поток должен выполнить это задание. Каждое задание должно быть выполнено ровно 1 раз
}
```
Сделать 2 реализации **`ThreadPool`**
1. **`FixedThreadPool`** - количество потоков задаётся в конструкторе и не меняется.
2. **`ScalableThreadPool`** - в конструкторе задаётся минимальное и максимальное (_int min, int max_) число потоков,
количество запущенных потоков может быть увеличено от минимального к максимальному, если при добавлении нового задания в очередь нет свободного потока для исполнения этого задания. При отсутствии задания в очереди, количество потоков снова должно быть уменьшено до значения _min_.

## Описание результатов

Для реализации данного задания был создан класс [**`Counter`**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/utils/Counter.java), который будет отвечать за предоставление задач для наших потоков.

```java
public class Counter {
    public Double count(double a) {
        for (int i = 0; i < 2; i++) {
            a += a;
        }

        return a;
    }
}
```
Затем согласно заданию был создан интерфейс [**`ThreadPool`**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/threadPool/ThreadPool.java). Он содержит в себе следующие методы:
1. **`void start()`** - запускает потоки. Потоки простаивают до тех пор, пока в очереди не появится новое задание (см. execute).
2. **`void execute(Runnable runnable)`** - помещает задачу в очередь. Свободный поток должен выполнить эту задачу. Каждая задача должна быть выполнена ровно 1 раз.
3. **`void interrupt()`** - используется для прерывания потока.


### 📌[FixedThreadPool](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/threadPool/FixedThreadPool.java)

Перейдём к реализации [**`FixedThreadPool`**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/threadPool/FixedThreadPool.java). В своей реализации имеет несколько полей:
- **`private final List<ThreadWorker> threads`** - список потоков;
- **`private final Queue<Runnable> taskQueue`** - очередь задач;
- **`private volatile boolean isRunning = true`** - флаг, проверяющий состояние потока.

### 🔀 Пара слов о **`volatile`**.

Это ключевое слово в _Java_, которое используется для полей класса. Оно указывает компилятору и виртуальной машине _Java_ на то, что значение переменной может быть изменено несколькими потоками и должно быть читаемым и записываемым напрямую из памяти, а не из кэша потока.

Основные аспекты **`volatile`**:

1. ***Видимость изменений***: когда переменная помечена как **`volatile`**, любое изменение этой переменной одним потоком сразу же становится видимым другим потокам. Это обеспечивает согласованность состояния переменной между потоками.

2. ***Запрет оптимизаций***: **`volatile`** также предотвращает оптимизации компилятора и виртуальной машины, которые могут привести к переупорядочиванию инструкций чтения и записи переменной.

Однако **`volatile`** не решает всех проблем многопоточности. Оно хорошо подходит для простых случаев, когда переменная используется только для чтения и записи (например, флаги состояния), но оно не гарантирует атомарность операций инкремента/декремента и не обеспечивает синхронизацию блоков кода.

###

Конструктор **`FixedThreadPool`** выглядит следующим образом:

```java
public FixedThreadPool(int poolSize) {
        if (poolSize <= 0) throw new IllegalArgumentException(
                format("Переданное в конструктор значение %d не является положительным!", poolSize));
        threads = new ArrayList<>(poolSize);
        taskQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < poolSize; i++) {
            threads.add(new ThreadWorker());
        }
    }
```
В большей степени нас интересует реализация очереди задач, в которой используется **`ConcurrentLinkedQueue`**. Это реализация интерфейса `Queue`, которая предоставляет потокобезопасную очередь, оптимизированную для эффективного использования в многопоточной среде. Она обеспечивает высокую производительность при одновременном доступе нескольких потоков к структуре данных.

**Преимущества** данной структуры данных: 

1. 🔒 **Потокобезопасность**. **`ConcurrentLinkedQueue`** реализует механизмы синхронизации, которые позволяют нескольким потокам безопасно добавлять, извлекать и проверять элементы очереди одновременно.

2. 🚀 **Высокая производительность**. Эта реализация обеспечивает высокую производительность при работе в многопоточной среде. Она использует неблокирующие алгоритмы, что делает её эффективной даже при высокой нагрузке.

3. 📈 **Отсутствие ограничений на размер**. **`ConcurrentLinkedQueue`** не имеет фиксированного размера, поэтому она может расти динамически в зависимости от добавляемых элементов.

4. 🔁 **Итераторы**. Итераторы, возвращаемые **`ConcurrentLinkedQueue`**, являются слабо-согласованными, что означает, что они не гарантируют отражение последних изменений в очереди. Однако они обеспечивают безопасное итерирование в многопоточной среде.

5. 🛡️ **Отсутствие блокировок**. **`ConcurrentLinkedQueue`** не использует блокировки для синхронизации операций, что снижает вероятность возникновения состязательных условий и блокировок.

Также здесь представлен вложенный класс **`ThreadWorker`**, который расширяет класс Thread и отвечает за выполнение задач, представленных в очереди.

### 📌[ScalableThreadPool](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/threadPool/ScalableThreadPool.java)

Перейдём к реализации [**`ScalableThreadPool`**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/threadPool/ScalableThreadPool.java). В своей реализации имеет несколько полей:
- **`private final int minPoolSize`** - минимальное количество потоков;
- **`private final int maxPoolSize`** - максимальное количество потоков;
- **`private final List<WorkerThread> threads`** - список потоков;
- **`private final Queue<Runnable> taskQueue`**- очередь задач;
- **`private volatile boolean isRunning = true`** - флаг, проверяющий состояние потока.

Также здесь представлен вложенный класс **`WorkerThread`**, который расширяет класс Thread и отвечает за выполнение задач, представленных в очереди.

Конструктор **`ScalableThreadPool`** выглядит следующим образом:

```java
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
```

### 📏 Вспомогательные классы 📎

В пакете [**task**](https://github.com/MironovNikita/sber-homework11/tree/main/src/main/java/org/example/task) расположены классы, отвечающие за пользовательский интерфейс в консоли - [**TaskHandler**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/task/TaskHandler.java) и класс [**TaskRunner**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/task/TaskRunner.java), отвечающий за запуск тестового кода.

### Протестируем в Main
🔬Проведём небольшое тестирование в классе [**`Main`**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/Main.java).

```java
public class Main {
    public static void main(String[] args) {
        TaskHandler taskHandler = new TaskHandler();
        taskHandler.run();
    }
}
```
В результате запуска программы получим следующее меню:

![mainMenu](https://github.com/MironovNikita/sber-homework11/blob/main/res/mainMenu.png)

Выберем проверку **`FixedThreadPool`**:
В данном задании выполняется 5 задач, а пул потоков содержит 3 потока.

```java
ThreadPool fixedThreadPool = new FixedThreadPool(3);

...
for (int i = 1; i <= 5; i++) {
            int threadNumber = i;

            fixedThreadPool.execute(() -> {
...
```

![task1](https://github.com/MironovNikita/sber-homework11/blob/main/res/task1.png)

Как можем видеть, все задачи выполнились нашим пулом потоков, а затем завершились, т.к. нам отобразилось меню дальнейшего выбора.

Перейдём к проверке **`ScalableThreadPool`**:
В данном задании выполняется 10 задач, а пул потока создаётся с минимальным количеством потоков - 2, а максимальным - 5.

```java
ThreadPool scalableThreadPool = new ScalableThreadPool(2, 5);

...
for (int i = 1; i <= 10; i++) {
            int threadNumber = i;

            scalableThreadPool.execute(() -> {
...
```

![task2](https://github.com/MironovNikita/sber-homework11/blob/main/res/task2.png)

Как видим из результатов, все задачи были выполнены, а количество потоков не превышало _пяти_ (_Thread-3 - Thread-7_).

⚠️ Подробнее с тестовыми методами можно ознакомиться в классе [**TaskRunner**](https://github.com/MironovNikita/sber-homework11/blob/main/src/main/java/org/example/task/TaskRunner.java). 📢

### Ни Main'ом единым 🌐

Также для проверки наших пулов потоков были написаны тесты, находящиеся в классах [**FixedThreadPoolTest**](https://github.com/MironovNikita/sber-homework11/blob/main/src/test/java/FixedThreadPoolTest.java) и [**ScalableThreadPoolTest**](https://github.com/MironovNikita/sber-homework11/blob/main/src/test/java/ScalableThreadPoolTest.java).
Данные классы содержат тесты, проверяющие:
- что все задачи, попадающие для выполнения нашим пулом потоком выполняются единожды;
- что все потоки завершают свою работу в результате вызова метода **`interrupt`**.

Результаты выполнения тестов для **`FixedThreadPool`**:

![fixedThreadTest](https://github.com/MironovNikita/sber-homework11/blob/main/res/fixedThreadTest.png)

Результаты выполнения тестов для **`ScalableThreadPool`**:

![scalableThreadTest](https://github.com/MironovNikita/sber-homework11/blob/main/res/scalableThreadTest.png)

Результаты выполнения всех тестов:

![allTests](https://github.com/MironovNikita/sber-homework11/blob/main/res/allTests.png)

Как видно из результатов, наши пулы потоков отрабатывают корректно. Все задачи выполняются единожды, а потоки прерываются в случае закрытия пула потоков.

### 💡 Примечание

Тесты написаны с помощью библиотеки JUnit (*junit-jupiter*). Соответствующая зависимость добавлена в [**`pom.xml`**](https://github.com/MironovNikita/sber-homework11/blob/main/pom.xml) 

Версия зависимости прописана в блоке *properties /properties*:

```java
<junit.version>5.10.1</junit.version>
```

Результат сборки проекта:

```java
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.344 s
[INFO] Finished at: 2024-02-07T23:49:27+03:00
[INFO] ------------------------------------------------------------------------
```






