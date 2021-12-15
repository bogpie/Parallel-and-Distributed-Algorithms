import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int noWorkers = Integer.parseInt(args[0]);
        String inputPath = args[1];
        String outputPath = args[2];

        Tema2 tema2 = new Tema2();
        Vector<MapTask> mapTasks = new Vector<>();
        mapTasks = tema2.mapStage(noWorkers, inputPath);
        mapTasks.forEach(mapTask -> System.out.println(mapTask.getResult()));
    }

    Vector<MapTask> mapStage(int noWorkers, String inputPath)
            throws FileNotFoundException {
        ExecutorService pool = Executors.newFixedThreadPool(noWorkers);
        Vector<MapTask> mapTasks = createMapTasks(inputPath, pool);

        for (MapTask mapTask : mapTasks) {
            pool.submit(mapTask);
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
            // Java warns in case of empty loop bodies
            if (pool.isTerminated()) {
                break;
            }
        }
        return mapTasks;
    }

    void initializeWorkers(int noWorkers, Vector<MapTask> mapTasks) {
        Vector<Worker> workers = new Vector<>();

        workers.setSize(noWorkers);
        for (int idWorker = 0; idWorker < workers.size(); ++idWorker) {
            workers.set(idWorker, new Worker());
        }
        assignMapTasks(mapTasks, workers);
        workers.forEach(worker -> System.out.println(worker.toString() + "\n"));

    }


    Vector<MapTask> createMapTasks(String inputPath, ExecutorService pool) throws FileNotFoundException {
        Vector<MapTask> mapTasks = new Vector<>();
        Scanner scanner = new Scanner(new File(inputPath));
        int fragmentSize = scanner.nextInt();
        int noDocuments = scanner.nextInt();
        scanner.nextLine(); // read new line
        Vector<Document> documents = new Vector<>();

        for (int idDocument = 0; idDocument < noDocuments; ++idDocument) {
            String path = scanner.nextLine();
            File file = new File(path);
            documents.add(new Document(file.getName(), (int) file.length()));
        }

        for (Document document : documents) {
            int offset = 0;

            while (document.getDimension() - offset > 0) {
                int crtFragmentSize = fragmentSize;
                if (document.getDimension() - offset - fragmentSize < 0) {
                    crtFragmentSize = document.getDimension() - offset;
                }

                MapTask mapTask =
                        new MapTask(document.getName(), offset,
                                crtFragmentSize, mapTasks.size(), pool);
                mapTasks.add(mapTask);
                offset += fragmentSize;
            }
        }

        return mapTasks;
    }


    private void assignMapTasks(Vector<MapTask> mapTasks, Vector<Worker> workers) {
        int idWorker = 0;
        for (MapTask mapTask : mapTasks) {
            Worker worker = workers.get((idWorker++) % workers.size());
            Vector<MapTask> workerMapTasks = worker.getMapTasks();
            workerMapTasks.add(mapTask);
        }
    }
}

