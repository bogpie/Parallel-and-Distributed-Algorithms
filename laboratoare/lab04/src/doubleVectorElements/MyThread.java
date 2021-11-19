package doubleVectorElements;

public class MyThread extends Thread {
    private final int idThread;
    private final int[] v;
    private final int P;

    public MyThread(int idThread, int[] v, int P) {
        this.idThread = idThread;
        this.v = v;
        this.P = P;
    }

    @Override
    public void run() {
        int N = v.length;
        int start = idThread * N / P;
        int end = Math.min((idThread + 1) * N / P, N);

        for (int i = start; i < end; ++i) {
            v[i] *= 2;
        }
    }
}
