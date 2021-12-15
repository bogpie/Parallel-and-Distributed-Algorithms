import java.util.Vector;

public class Worker {
    private Vector<MapTask> mapTasks;

    public Worker() {
        mapTasks = new Vector<>();
    }

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
