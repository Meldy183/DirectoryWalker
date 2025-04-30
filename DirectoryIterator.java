import java.util.Stack;

//iterator pattern implementation
public class DirectoryIterator implements Iterator<Node> {
    private final Stack<Pair<Integer, Node>> stack = new Stack<>();

    public DirectoryIterator(Directory node) {
        this.stack.push(new Pair<>(0, node));
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public Node next() {
        Stack<Pair<Integer, Node>> temp_stack = new Stack<>();
        if (hasNext()) {
            Pair<Integer, Node> pair = stack.pop();
            pair.node.depth = pair.path;
            if (pair.node.getClass() == Directory.class) {
                Directory x = (Directory) pair.node;
                for (Node child : x.children) {
                    temp_stack.push(new Pair<>(pair.path + 1, child));
                }
                while (!temp_stack.isEmpty()) {
                    stack.push(temp_stack.pop());
                }
            }
            return pair.node;
        }
        return null;
    }
}
