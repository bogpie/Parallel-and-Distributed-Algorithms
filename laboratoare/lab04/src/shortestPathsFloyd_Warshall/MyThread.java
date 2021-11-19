package shortestPathsFloyd_Warshall;

public class MyThread extends Thread {
    private final int idThread;
    private final int[][] graph;
    private final int P;

    public MyThread(int idThread, int[][] graph, int P) {
        this.idThread = idThread;
        this.graph = graph;
        this.P = P;
    }

    @Override
    public void run() {
        int N = graph.length;
        int start = idThread * N / P;
        int end = Math.min((idThread + 1) * N / P, N);

        for (int k = start; k < end; k++) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
                }
            }
        }
    }
}
