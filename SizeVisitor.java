// total size counter
public class SizeVisitor implements Visitor {
    private double total = 0.0;

    @Override
    public void visit(File file) {
        total += file.getSize();
    }

    @Override
    public void visit(Directory dir) {
        total += 0;
    }

    public double getTotalSize() {
        return total;
    }
}
