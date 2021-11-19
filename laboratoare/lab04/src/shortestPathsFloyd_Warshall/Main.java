package shortestPathsFloyd_Warshall;

public class Main {
    public static int NUMBER_OF_THREADS;

    public static void main(String[] args) {
        int M = 9;
        int[][] graph = {{0, 1, M, M, M},
                {1, 0, 1, M, M},
                {M, 1, 0, 1, 1},
                {M, M, 1, 0, M},
                {M, M, 1, M, 0}};

        NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];

//        // Parallelize me (You might want to keep the original code in order to compare)
//        for (int k = 0; k < 5; k++) {
//            for (int i = 0; i < 5; i++) {
//                for (int j = 0; j < 5; j++) {
//                    graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
//                }
//            }
//        }

        for (int idThread = 0; idThread < NUMBER_OF_THREADS; ++idThread) {
            threads[idThread] = new MyThread(idThread, graph, NUMBER_OF_THREADS);
            threads[idThread].start();
        }

        for (int idThread = 0; idThread < NUMBER_OF_THREADS; idThread++) {
            try {
                threads[idThread].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }
    }
}
