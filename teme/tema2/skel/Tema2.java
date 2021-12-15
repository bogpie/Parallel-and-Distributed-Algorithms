import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Tema2 {
    Vector<Document> documents;
    int fragmentDimension;

    public Tema2() {
        documents = new Vector<>();
        fragmentDimension = 0;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int noWorkers = Integer.parseInt(args[0]);
        String inputPath = args[1];
        String outputPath = args[2];

        Tema2 tema2 = new Tema2();
        Vector<MapTask> mapTasks = tema2.mapStage(noWorkers, inputPath);
        Vector<MapTaskResult> mapTaskResults =
                mapTasks.stream().
                        map(MapTask::getResult).
                        collect(Collectors.toCollection(Vector::new));

        Vector<ReduceTask> reduceTasks = tema2.reduceStage(mapTaskResults);
        reduceTasks.forEach(System.out::println);
    }

    Vector<ReduceTask> reduceStage(Vector<MapTaskResult> mapTaskResults) {
        Vector<ReduceTask> reduceTasks = new Vector<>();
        for (Document document : documents) {
            reduceTasks.add(new ReduceTask(document));
        }

        for (MapTaskResult mapTaskResult : mapTaskResults) {
            ReduceTask reduceTask =
                    reduceTasks.stream().filter(reduceTaskInFilter ->
                            reduceTaskInFilter.getDocument().getName().equals(mapTaskResult.getName()))
                            .collect(Collectors.toList())
                            .get(0);
            reduceTask.getMapTaskResults().add(mapTaskResult);
        }
        return reduceTasks;
    }

    Vector<MapTask> mapStage(int noWorkers, String inputPath)
            throws FileNotFoundException {
        ExecutorService pool = Executors.newFixedThreadPool(noWorkers);
        Vector<MapTask> mapTasks = createMapTasks(inputPath);

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


    Vector<MapTask> createMapTasks(String inputPath)
            throws FileNotFoundException {
        Vector<MapTask> mapTasks = new Vector<>();
        documents = getDocuments(inputPath);

        for (Document document : documents) {
            int offset = 0;

            while (document.getDimension() - offset > 0) {
                int crtFragmentSize = fragmentDimension;
                if (document.getDimension() - offset - fragmentDimension < 0) {
                    crtFragmentSize = document.getDimension() - offset;
                }

                MapTask mapTask =
                        new MapTask(document.getName(), offset,
                                crtFragmentSize, mapTasks.size());
                mapTasks.add(mapTask);
                offset += fragmentDimension;
            }
        }

        return mapTasks;
    }

    Vector<Document> getDocuments(String inputPath)
            throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(inputPath));
        fragmentDimension = scanner.nextInt();
        int noDocuments = scanner.nextInt();
        scanner.nextLine(); // read new line
        Vector<Document> documents = new Vector<>();

        for (int idDocument = 0; idDocument < noDocuments; ++idDocument) {
            String path = scanner.nextLine();
            File file = new File(path);
            documents.add(new Document(file.getName(), (int) file.length()));
        }
        return documents;
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

