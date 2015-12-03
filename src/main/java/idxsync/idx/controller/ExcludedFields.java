package idxsync.idx.controller;

import idxsync.idx.service.FieldQuery;

import java.util.List;

public class ExcludedFields {

    private List<FieldQuery> excludedFields;

    public List<FieldQuery> getExcludedFields() {
        return excludedFields;
    }

    public void setExcludedFields(List<FieldQuery> excludedFields) {
        this.excludedFields = excludedFields;
    }
}
