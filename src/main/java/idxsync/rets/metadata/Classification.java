package idxsync.rets.metadata;

import java.util.ArrayList;
import java.util.List;

public class Classification {

    private String name;
    private String description;

    private List<Table> tableList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Table> getTableList() {
        if (tableList == null) {
            tableList = new ArrayList<>();
        }

        return tableList;
    }
}
