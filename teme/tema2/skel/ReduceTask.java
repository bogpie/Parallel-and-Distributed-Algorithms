import java.util.Vector;

public class ReduceTask {
    private Document document;
    private Vector<MapTaskResult> mapTaskResults;

    public ReduceTask(Document document) {
        this.document = document;
        mapTaskResults = new Vector<>();
    }

    public ReduceTask() {
        this.mapTaskResults = new Vector<>();
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Vector<MapTaskResult> getMapTaskResults() {
        return mapTaskResults;
    }

    public void setMapTaskResults(Vector<MapTaskResult> mapTaskResults) {
        this.mapTaskResults = mapTaskResults;
    }

    @Override
    public String toString() {
        return document.toString() + ":\n" + mapTaskResults + "\n";
    }
}
