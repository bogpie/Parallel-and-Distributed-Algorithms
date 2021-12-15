import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

class MapTaskResult {
    private final String name;

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    private Dictionary dictionary;
    private Vector<String> maximalWords;

    public MapTaskResult(String name) {
        this.name = name;
        this.dictionary = new Dictionary();
        maximalWords = new Vector<>();
    }

    public MapTaskResult(String name, Dictionary dictionary,
                         Vector<String> maximalWords) {
        this.name = name;
        this.dictionary = dictionary;
        this.maximalWords = maximalWords;
    }

    public Vector<String> getMaximalWords() {
        return maximalWords;
    }

    public void setMaximalWords(Vector<String> maximalWords) {
        this.maximalWords = maximalWords;
    }

    @Override
    public String toString() {
        return "MapTaskResult{" +
                "name='" + name + '\'' +
                ", dictionary=" + dictionary +
                ", maximalWords=" + maximalWords +
                '}';
    }
}


public class MapTask implements Runnable {
    private final String name;
    private final int offset;
    private final int dimension;
    private final int index;
    private final ExecutorService pool;
    private MapTaskResult result;

    public MapTask(String name, int offset, int dimension, int index,
                   ExecutorService pool) {
        this.name = name;
        this.offset = offset;
        this.dimension = dimension;
        this.index = index;
        this.pool = pool;
    }

    public MapTaskResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "MapTask{" + index + ": " +
                "name='" + name + '\'' +
                ", offset=" + offset +
                ", dimension=" + dimension +
                '}';
    }

    @Override
    public void run() {
        String userDir = System.getProperty("user.dir");
        String path = userDir + "/tests/files/" + name;
        String delimiters = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n\0";

        result = new MapTaskResult(name);
        try {
            RandomAccessFile file = new RandomAccessFile(path, "r");

            // Read a letter before to check whether first word is complete
            byte[] bytes = new byte[dimension + 1];
            if (offset != 0) {
                file.seek(offset - 1);
            }
            file.read(bytes, 0, dimension + 1);

            String string = new String(bytes);
            String lastCharacter = string.substring(string.length() - 1);

            // Eliminate letters from string until it starts with an actual word
            if (offset != 0) {
                String firstLetter = string.substring(0, 1);
                while (!delimiters.contains(firstLetter)) {
                    string = string.substring(1);
                    firstLetter = string.substring(0, 1);
                }
            }

            // Keep reading until reaching a delimiter
            while (!delimiters.contains(lastCharacter)) {
                bytes = new byte[1];
                file.read(bytes, 0, 1);
                lastCharacter = new String(bytes);
                string = string.concat(lastCharacter);
            }

            StringTokenizer tokenizer =
                    new StringTokenizer(string, delimiters);

            int maximum = -1;
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
                // System.out.println(index + 1 + ": " + word);
                int length = word.length();
                if (length > maximum) {
                    maximum = length;
                    result.getMaximalWords().clear();
                    result.getMaximalWords().add(word);
                }
                if (!result.getDictionary().getMap().containsKey(length)) {
                    result.getDictionary().getMap().put(length, 1);
                } else {
                    int noWords = result.getDictionary().getMap().get(length);
                    result.getDictionary().getMap().put(length, noWords + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
