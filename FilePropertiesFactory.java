import java.util.HashMap;

// i use singleton because:
// 1) i do not need a second instance of this connection/referencer class
// 2) i want to memorize patterns via practise so that despite it might be not needed
// here i think it is better to use singleton here
public class FilePropertiesFactory {
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
