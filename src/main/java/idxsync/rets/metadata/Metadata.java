package idxsync.rets.metadata;


import java.util.LinkedHashMap;
import java.util.Map;

public class Metadata {

    private String systemId;
    private String systemDescription;

    private Map<String, Resource> resources;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemDescription() {
        return systemDescription;
    }

    public void setSystemDescription(String systemDescription) {
        this.systemDescription = systemDescription;
    }

    public Map<String, Resource> getResources() {
        if (resources == null) {
            resources = new LinkedHashMap<>();
        }

        return resources;
    }
}
