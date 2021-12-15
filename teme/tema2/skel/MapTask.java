import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;
import java.util.Vector;


public class MapTask implements Runnable {
    private final String name;
    private final int offset;
    private final int dimension;
    private final int index;
    private MapTaskResult result;

    public MapTask(String name, int offset, int dimension, int index) {
        this.name = name;
        this.offset = offset;
        this.dimension = dimension;
        this.index = index;
    }

    public String getName() {
        return name;
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
            int newDimension = dimension;
            if (offset != 0) {
                ++newDimension;
                file.seek(offset - 1);
            }
            byte[] bytes = new byte[newDimension];
            file.read(bytes, 0, newDimension);

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

class MapTaskResult {
    private String name;
    private Dictionary dictionary;
    private Vector<String> maximalWords;

    public MapTaskResult() {
        this("");
        dictionary = new Dictionary();
        maximalWords = new Vector<>();
    }

    public MapTaskResult(String name) {
        this.name = name;
        dictionary = new Dictionary();
        maximalWords = new Vector<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public Vector<String> getMaximalWords() {
        return maximalWords;
    }

    @Override
    public String toString() {
        return "MapTaskResult{" +
                "name='" + name + '\'' +
                ", dictionary=" + dictionary +
                ", maximalWords=" + maximalWords +
                "}\n";
    }
}
