import java.util.Comparator;
import java.util.TreeMap;

public class Dictionary {
    private final TreeMap<Integer, Integer> map;

    public Dictionary() {
        map = new TreeMap<>(Comparator.reverseOrder());
    }

    public TreeMap<Integer, Integer> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
