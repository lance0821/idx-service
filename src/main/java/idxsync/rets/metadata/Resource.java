package idxsync.rets.metadata;


import java.util.LinkedHashMap;
import java.util.Map;

public class Resource {

    private String id;
    private String description;

    private Map<String, Classification> classifications;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Classification> getClassifications() {
        if (classifications == null) {
            classifications = new LinkedHashMap<>();
        }

        return classifications;
    }
}
