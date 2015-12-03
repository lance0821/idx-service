package idxsync.idx.controller;

import idxsync.idx.service.FieldQuery;

import java.util.List;

public class IncludedFields {

    private List<FieldQuery> includedFields;

    public List<FieldQuery> getIncludedFields() {
        return includedFields;
    }

    public void setIncludedFields(List<FieldQuery> includedFields) {
        this.includedFields = includedFields;
    }
}
