import java.util.LinkedList;

// a base class for a file system node.
// represents a non-terminal node holding a list of children
public class Directory extends Node {
    public final LinkedList<Node> children = new LinkedList<>();
    final int id;

    void addChild(Node child) {
        children.add(child);
    }

    public Directory(String name, Integer parent_id, int id) {
        super(name, parent_id == null ? -1 : parent_id);
        this.id = id;
    }

    public Directory(String name, Integer id) {
        super(name, 0);
        this.id = id;
    }

    public DirectoryIterator createIterator() {
        return new DirectoryIterator(this);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
        for (Node child : children) {
            child.accept(v);
        }
    }
}
