import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// facade pattern used to handle everything in one class
public class Handler {
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
