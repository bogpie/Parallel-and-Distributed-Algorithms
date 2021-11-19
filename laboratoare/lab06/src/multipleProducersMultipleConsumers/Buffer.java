package multipleProducersMultipleConsumers;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
    public static final int BUFFER_SIZE = 4;
    public final static ArrayBlockingQueue<Integer> queue =
            new ArrayBlockingQueue<Integer>(BUFFER_SIZE);

    public Buffer() {
    }

    public void put(int value) {
        try {
            queue.put(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int get() {
        int result = -1;
        try {
            result = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
