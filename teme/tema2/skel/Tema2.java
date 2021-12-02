import java.io.*;
import java.util.*;

public class Tema2 {
    Vector<MapTask> createMapTasks(String inputPath) throws FileNotFoundException {
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

                MapTask mapTask = new MapTask(document.getName(), offset, crtFragmentSize);
                mapTasks.add(mapTask);
                offset += fragmentSize;
            }
        }

        return mapTasks;
    }

    private void printMapTasks(Vector<MapTask> mapTasks) {
        for (MapTask task : mapTasks) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        String inputPath = args[1];
        String outputPath = args[2];

        Tema2 tema2 = new Tema2();
        Vector<MapTask> mapTasks;
        mapTasks = tema2.createMapTasks(inputPath);
        tema2.printMapTasks(mapTasks);

        FileWriter fileWriter = new FileWriter(outputPath);
        fileWriter.close();
    }
}

