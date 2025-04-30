// helper class for stack and better building the tree
public class Pair<P, N> {
    final P path;
    final N node;

    Pair(P path, N value) {
        this.path = path;
        this.node = value;
    }
}
