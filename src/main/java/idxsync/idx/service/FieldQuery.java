package idxsync.idx.service;

import java.util.ArrayList;
import java.util.List;

public class FieldQuery {
    private String field;
    private List<String> values;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getValues() {

        if (values == null) {
            values = new ArrayList<>();
        }

        return values;
    }
}
