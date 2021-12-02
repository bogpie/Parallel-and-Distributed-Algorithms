import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.*;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        String inputPath = args[1];
        String outputPath = args[2];

        Scanner scanner = new Scanner(new File(inputPath));
        int fragmentSize = scanner.nextInt();
        int noDocuments = scanner.nextInt();
        String newLine = scanner.nextLine();
        Vector<Document> documents = new Vector<>();

        for (int idDocument = 0; idDocument < noDocuments; ++idDocument) {
            String path = scanner.nextLine();
            File file = new File(path);
            documents.add(new Document(file.getName(), (int) file.length()));
        }

        Vector<MapTask> mapTasks = new Vector<>();

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

        for (MapTask task : mapTasks) {
            System.out.println(task);
        }

        FileWriter fileWriter = new FileWriter(outputPath);
        fileWriter.close();
    }
}

