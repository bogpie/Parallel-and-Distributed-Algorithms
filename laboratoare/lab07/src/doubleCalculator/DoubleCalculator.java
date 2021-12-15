package doubleCalculator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DoubleCalculator {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Future<Integer> calculate(int input) {
        CompletableFuture<Integer> task = new CompletableFuture<>();
        executorService.submit(() -> {
                    Thread.sleep(2000);
                    task.complete(2 * input);
                    return null;
                }
        );

        return task;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
