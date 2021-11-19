package oneProducerOneConsumer;

public class Buffer {
    private int a;
	boolean isFull = false;

	synchronized void put(int value) {
		while(isFull) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		a = value;
		isFull = true;
		notify();
	}

	synchronized int get() {
		while (!isFull) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isFull = false;
		notify();
		return a;
	}
}
