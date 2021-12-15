package sampleExecutorServiceCompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService tpe = Executors.newFixedThreadPool(4);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        AtomicInteger counter = new AtomicInteger(0);
        counter.incrementAndGet();

        String userDir = System.getProperty("user.dir");
        tpe.submit
                (new MyRunnable(tpe, userDir + "/laboratoare/lab07/files",
                        "file2.txt",
                        counter,
                        completableFuture));

        String result = completableFuture.get();

        System.out.println("Result acquired");

        if (result != null) {
            System.out.println("File was found at this path: " + result);
        } else {
            System.out.println("File was not found");
        }
    }
}
