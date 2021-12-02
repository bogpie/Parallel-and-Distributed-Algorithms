public class MapTask {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    private String name;
    private int offset;
    private int dimension;

    public MapTask(String name, int offset, int dimension) {
        this.name = name;
        this.offset = offset;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "MapTask{" +
                "name='" + name + '\'' +
                ", offset=" + offset +
                ", dimension=" + dimension +
                '}';
    }
}
