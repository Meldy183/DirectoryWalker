// base class
public class Node {
    protected String name;
    protected Integer parent_id;
    public int depth = 0;

    public Node(String name, int parent_id) {
        this.name = name;
        this.parent_id = parent_id;
    }

    public void accept(Visitor v) {
    }
}
