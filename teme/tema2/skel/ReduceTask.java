import java.util.Vector;

public class ReduceTask {
    private Vector<Dictionary> dictionaries;
    private Vector<String> maximalWords;

    public ReduceTask() {
        this.dictionaries = new Vector<>();
        this.maximalWords = new Vector<>();
    }

    public ReduceTask(Vector<Dictionary> dictionaries, Vector<String> maximalWords) {
        this.dictionaries = dictionaries;
        this.maximalWords = maximalWords;
    }

    public Vector<Dictionary> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(Vector<Dictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public Vector<String> getMaximalWords() {
        return maximalWords;
    }

    public void setMaximalWords(Vector<String> maximalWords) {
        this.maximalWords = maximalWords;
    }
}
