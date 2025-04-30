// just data class for storing data, in File i will have a pointer to an instance through FilePropertyFactory
public class FileProperties {
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
