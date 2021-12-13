public class MapTask {
    private String name;
    private int offset;
    private int dimension;
    private int index;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public MapTask(String name, int offset, int dimension) {
        this.name = name;
        this.offset = offset;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "MapTask{" + index + ": " +
                "name='" + name + '\'' +
                ", offset=" + offset +
                ", dimension=" + dimension +
                '}';
    }
}
