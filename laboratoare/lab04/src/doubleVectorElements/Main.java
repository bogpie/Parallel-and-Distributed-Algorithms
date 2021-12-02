package doubleVectorElements;

public class Main {
    public static int NUMBER_OF_THREADS;

    public static void main(String[] args) {
        int N = 100000013;
        int[] v = new int[N];
        NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];

        for (int i = 0; i < N; i++) {
            v[i] = i;
        }

        for (int idThread = 0; idThread < NUMBER_OF_THREADS; ++idThread){
            threads[idThread] = new MyThread(idThread, v, NUMBER_OF_THREADS);
            threads[idThread].start();
        }
        for (int idThread = 0; idThread < NUMBER_OF_THREADS; idThread++) {
            try {
                threads[idThread].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < N; i++) {
            if (v[i] != i * 2) {
                System.out.println("Wrong answer");
                System.exit(1);
            }
        }
        System.out.println("Correct");
    }

}
