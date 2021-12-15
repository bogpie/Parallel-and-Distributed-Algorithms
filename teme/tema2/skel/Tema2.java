import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
        Vector<MapTaskResult> mapTasksResults = tema2.mapStage(noWorkers,
                inputPath);

        Vector<ReduceTaskResult> reduceTaskResults = tema2.reduceStage(noWorkers,
                mapTasksResults);

        reduceTaskResults.sort(
                // We use a multiplication trick for better precision
                (result1, result2) -> (int) ((result2.getRank() - result1.getRank()) * 100)
        );


        FileWriter fileWriter = new FileWriter(outputPath);
        reduceTaskResults.forEach(reduceTaskResult -> {
                    try {
                        fileWriter.append(reduceTaskResult.toString()).append("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        fileWriter.close();
    }

    Vector<ReduceTask> createReduceTasks(Vector<MapTaskResult> mapTaskResults) {
        Vector<ReduceTask> reduceTasks = new Vector<>();
        for (Document document : documents) {
            reduceTasks.add(new ReduceTask(document));
        }

        for (MapTaskResult mapTaskResult : mapTaskResults) {
            ReduceTask reduceTask =
                    reduceTasks.stream().filter(reduceTaskInFilter ->
                            reduceTaskInFilter.
                                    getDocument().
                                    getName().
                                    equals(mapTaskResult.getName()))
                            .collect(Collectors.toList())
                            .get(0);
            reduceTask.getMapTaskResults().add(mapTaskResult);
        }
        return reduceTasks;
    }

    Vector<ReduceTaskResult> reduceStage(int noWorkers,
                                         Vector<MapTaskResult> mapTaskResults) {
        Vector<ReduceTask> reduceTasks = createReduceTasks(mapTaskResults);
        ExecutorService pool = Executors.newFixedThreadPool(noWorkers);

        for (ReduceTask reduceTask : reduceTasks) {
            pool.submit(reduceTask);
        }
        pool.shutdown();

        while (!pool.isTerminated()) {
            // Java warns in case of empty loop bodies
            if (pool.isTerminated()) {
                break;
            }
        }
        return reduceTasks.stream()
                .map(ReduceTask::getResult)
                .collect(Collectors.toCollection(Vector::new));
    }

    Vector<MapTaskResult> mapStage(int noWorkers, String inputPath)
            throws FileNotFoundException {
        Vector<MapTask> mapTasks = createMapTasks(inputPath);
        ExecutorService pool = Executors.newFixedThreadPool(noWorkers);

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
        return mapTasks.stream()
                .map(MapTask::getResult)
                .collect(Collectors.toCollection(Vector::new));
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
}

