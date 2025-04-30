import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        Handler handler = new Handler();
        for (int i = 0; i < n; i++) {
            String s = scanner.nextLine();
            if (Validator.validate(s)) {
                handler.takeInput(s);
            }
        }
        handler.build();
    }
}

// validates length
class Validator {
    static Boolean validate(String input) {
        String[] parse = input.split(" ");
        if (parse[0].equals("DIR")) {
            return (parse.length == 3 || parse.length == 4);
        }
        if (parse[0].equals("FILE")) {
            return parse.length == 7;
        }
        return false;
    }
}

// facade pattern used to handle everything in one class
class Handler {
    Directory root;
    // i will use hashMap for faster access without n^5 times traversing the tree when add an elem
    // <id : Directory object>
    HashMap<Integer, Directory> directories = new HashMap<>();

    // validates input by words
    void takeInput(String input) {
        String[] parse = input.split(" ");

        switch (parse[0]) {
            case "DIR":
                addDirectory(parse);
                break;
            case "FILE":
                String[] parts = parse[6].split("\\.");
                if (parts.length < 2) return;
                String name = parts[0];
                String ext = parts[1];
                addFile(Arrays.copyOfRange(parse, 0, 6), name, ext);
                break;
            default:
        }
    }

    // adds directory
    void addDirectory(String[] info) {
        Directory directory;
        if (info.length == 3) {
            directory = new Directory(info[2], Integer.parseInt(info[1]));
        } else {
            directory = new Directory(info[3], Integer.parseInt(info[2]), Integer.parseInt(info[1]));
        }
        directories.put(directory.id, directory);
        Directory parent = directories.get(directory.parent_id);
        if (parent != null) {
            parent.addChild(directory);
        }
    }

    // adds file
    void addFile(String[] info, String name, String ext) {
        boolean flag = false;
        switch (info[2]) {
            case "T":
                flag = true;
                break;
            case "F":
                break;
            default:
        }
        File file = new File(Integer.parseInt(info[1]),
                flag, info[3], info[4], Double.parseDouble(info[5]), name, ext);
        Directory parent = directories.get(file.parent_id);
        if (parent != null) {
            parent.addChild(file);
        }

    }

    public Handler() {
        root = new Directory(".", null, 0);
        directories.put(0, root);
    }

    // prints total and tree linux styled
    public void build() {
        SizeVisitor sizeVisitor = new SizeVisitor();
        root.accept(sizeVisitor);
        double sum = sizeVisitor.getTotalSize();
        System.out.println("total: " + ceil(sum) + "KB");
        System.out.println(root.name);
        print();
    }

    private void print() {
        DirectoryIterator it = root.createIterator();
        if (it.hasNext()) {
            it.next();
        }
        List<Boolean> isLastAtDepth = new ArrayList();
        isLastAtDepth.add(true);

        while (it.hasNext()) {
            Node node = it.next();
            int depth = node.depth;

            // Determine if this node is the last child of its parent
            Directory parent = directories.get(node.parent_id);
            boolean isLast = false;
            if (parent != null) {
                List<Node> siblings = parent.children;
                isLast = !siblings.isEmpty() && siblings.get(siblings.size() - 1) == node;
            }

            if (depth < isLastAtDepth.size()) {
                isLastAtDepth.set(depth, isLast);
            } else {
                isLastAtDepth.add(isLast);
            }

            while (isLastAtDepth.size() > depth + 1) {
                isLastAtDepth.remove(isLastAtDepth.size() - 1);
            }

            StringBuilder prefix = new StringBuilder();
            for (int lvl = 1; lvl < depth; lvl++) {
                prefix.append(isLastAtDepth.get(lvl) ? "    " : "│   ");
            }
            prefix.append(isLast ? "└── " : "├── ");

            if (node.getClass() == File.class) {
                File f = (File) node;
                System.out.println(prefix + f.getName() + "." + f.getExt()
                        + " (" + ceil(f.getSize()) + "KB)");
            } else {
                System.out.println(prefix + node.name);
            }
        }
    }


    // hate writing ceiling but more than that looking for the bugs with that without knowing it
    public static String ceil(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }

        // For decimal numbers, remove trailing zeros
        String str = String.format("%.10f", value).replaceAll("0*$", "");
        if (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }
        return str.replaceAll(",", ".");
    }
}

// visitor pattern implementation
interface Visitor {
    void visit(File file);
    void visit(Directory dir);
}

// a base class for a file system node.
// represents a non-terminal node holding a list of children
class Directory extends Node {
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

// represents a terminal node holding properties of a single file
class File extends Node {
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

// total size counter
class SizeVisitor implements Visitor {
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

// just data class for storing data, in File i will have a pointer to an instance through FilePropertyFactory
class FileProperties {
    private final String extension;
    private final String owner;
    private final String group;
    private final Boolean permission;

    public FileProperties(String extension, String owner, String group, Boolean permission) {
        this.extension = extension;
        this.owner = owner;
        this.group = group;
        this.permission = permission;
    }

    public String extension() {
        return extension;
    }

    public String owner() {
        return owner;
    }

    public String group() {
        return group;
    }

    public Boolean permission() {
        return permission;
    }
}


// i use singleton because:
// 1) i do not need a second instance of this connection/referencer class
// 2) i want to memorize patterns via practise so that despite it might be not needed
// here i think it is better to use singleton here
class FilePropertiesFactory {
    private static final FilePropertiesFactory INSTANCE = new FilePropertiesFactory();

    private FilePropertiesFactory() {
    }

    public static FilePropertiesFactory getInstance() {
        return INSTANCE;
    }

    final private HashMap<String, FileProperties> map = new HashMap<>();

    public FileProperties getFileProperties(String extension, String owner, String group, Boolean permission) {
        String key = extension + '!' + owner + '!' + group + '!' + permission;
        if (!map.containsKey(key)) {
            FileProperties fileProperties = new FileProperties(extension, owner, group, permission);
            map.put(key, fileProperties);
        }
        return map.get(key);
    }
}

interface Iterator<Directory> {
    boolean hasNext();

    Directory next();
}


//iterator pattern implementation
class DirectoryIterator implements Iterator<Node> {
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


// helper class for stack and better building the tree
class Pair<P, N> {
    final P path;
    final N node;

    Pair(P path, N value) {
        this.path = path;
        this.node = value;
    }
}

// base class
class Node {
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