import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class ReduceTask implements Runnable {
    private final Document document;
    private final Vector<MapTaskResult> mapTaskResults;
    private final MapTaskResult combinedMapTaskResult;
    private ReduceTaskResult reduceTaskResult;

    public ReduceTask(Document document) {
        this.document = document;
        mapTaskResults = new Vector<>();
        combinedMapTaskResult = new MapTaskResult();
        reduceTaskResult = new ReduceTaskResult();
    }

    public ReduceTaskResult getResult() {
        return reduceTaskResult;
    }

    public Document getDocument() {
        return document;
    }

    public Vector<MapTaskResult> getMapTaskResults() {
        return mapTaskResults;
    }

    @Override
    public String toString() {
        return document.toString() + ":\n" + mapTaskResults + "\n";
    }

    @Override
    public void run() {
        var destination = combinedMapTaskResult.getDictionary().getMap();
        mapTaskResults.forEach(mapTaskResult -> {
                    var source = mapTaskResult.getDictionary().getMap();
                    source.forEach((length, noWords) ->
                            destination.merge(length, noWords, Integer::sum));
                }
        );
        combinedMapTaskResult.setName(mapTaskResults.get(0).getName());

        // Firstly add all the words
        mapTaskResults.forEach(mapTaskResult -> combinedMapTaskResult.getMaximalWords().addAll(mapTaskResult.getMaximalWords())
        );


        // Then select only the maximum-lengthened ones
        int maximumLength =
                combinedMapTaskResult.getDictionary().getMap().firstKey();
        combinedMapTaskResult
                .getMaximalWords()
                .removeIf(s -> s.length() < maximumLength);

        float rank = calculateRank(combinedMapTaskResult.getDictionary().getMap());

        var firstEntry =
                combinedMapTaskResult.getDictionary().getMap().firstEntry();
        reduceTaskResult = new ReduceTaskResult(
                rank,
                firstEntry.getKey(),
                firstEntry.getValue(),
                getDocument().getName()
        );
    }

    float calculateRank(TreeMap<Integer, Integer> map) {
        var values =
                new Vector<>(map.values());
        int totalWords = values
                .stream()
                .mapToInt(Integer::valueOf)
                .sum();

        float rank = 0;

        Vector<Integer> fibonacci = calculateFibonacci(map.firstKey() + 2);

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            rank += fibonacci.get(entry.getKey() + 1) * entry.getValue();
        }

        rank = rank / (totalWords * 1.0F);

        return rank;
    }

    Vector<Integer> calculateFibonacci(int value) {
        Vector<Integer> fibonacci = new Vector<>();
        fibonacci.add(0);
        fibonacci.add(1);
        int first = 0;
        int second = 1;
        value -= 2;
        while (value > 0) {
            int third = first + second;
            fibonacci.add(third);
            first = second;
            second = third;
            --value;
        }
        return fibonacci;
    }
}

class ReduceTaskResult {
    private final int maximumLength;
    private final int noWords;
    private final String name;
    private float rank;

    public ReduceTaskResult() {
        rank = 0;
        maximumLength = 0;
        noWords = 0;
        name = "";
    }

    public ReduceTaskResult(float rank, int maximumLength, int noWords,
                            String name) {
        this.rank = rank;
        this.maximumLength = maximumLength;
        this.noWords = noWords;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return getName() + ","
                + String.format("%.2f", rank).replace(",", ".") + ","
                + maximumLength + "," + noWords;
    }
}
