// represents a terminal node holding properties of a single file
public class File extends Node {
    private final FileProperties properties;
    private final Double size;

    public File(int parent_id, Boolean permission, String owner, String group, double size, String name, String ext) {
        super(name, parent_id);
        properties = FilePropertiesFactory.getInstance().getFileProperties(ext, owner, group, permission);
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return properties.extension();
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
