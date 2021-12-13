import java.util.Vector;

public class Worker {
    public Worker() {
        mapTasks = new Vector<>();
    }

    private Vector<MapTask> mapTasks;

    public Vector<MapTask> getMapTasks() {
        return mapTasks;
    }

    public void setMapTasks(Vector<MapTask> mapTasks) {
        this.mapTasks = mapTasks;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "mapTasks=" + mapTasks +
                '}';
    }
}
