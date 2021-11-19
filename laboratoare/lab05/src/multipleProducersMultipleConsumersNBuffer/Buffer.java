package multipleProducersMultipleConsumersNBuffer;

import java.util.Queue;

public class Buffer {

    LimitedQueue<Integer> queue;
    int limit;

    public Buffer(int size) {
        queue = new LimitedQueue<>(size);
        limit = size;
    }

    public boolean isFull(LimitedQueue queue) {
        return queue.size() == limit;
    }

    synchronized public void put(int value) {
        while (isFull(queue)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        queue.add(value);
        notifyAll();
    }

    synchronized public int get() {
        int a = -1;
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
        Integer result = queue.poll();

        if (result != null) {
            a = result;
        }
        return a;
    }
}
