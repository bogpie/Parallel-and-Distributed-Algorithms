public class Document {
    private String name;
    private int dimension;

    public Document(String name, int dimension) {
        this.name = name;
        this.dimension = dimension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return "Document{" +
                "name='" + name + '\'' +
                ", dimension=" + dimension +
                '}';
    }
}
