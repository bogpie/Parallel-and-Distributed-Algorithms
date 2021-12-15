package sampleExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(4);

        inQueue.incrementAndGet();
        String userDir = System.getProperty("user.dir");
        tpe.submit(new MyRunnable
                (userDir + "/laboratoare/lab07/files", tpe, inQueue));

        while (inQueue.get() != 0) {
            System.out.println(inQueue);

        }
    }
}
