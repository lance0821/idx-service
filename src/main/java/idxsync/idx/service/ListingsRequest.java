package idxsync.idx.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class ListingsRequest {

    private String locationQuery;
    private List<FieldQuery> includedFields;
    private List<FieldQuery> excludedFields;
    private List<RangeQuery> ranges;
    private PageConfig pageConfig;
    private SortConfig sortConfig;

    public String getLocationQuery() {
        return locationQuery;
    }

    public void setLocationQuery(String locationQuery) {
        this.locationQuery = locationQuery;
    }

    public List<FieldQuery> getIncludedFields() {
        if (includedFields == null) {
            includedFields = new ArrayList<>();
        }
        return includedFields;
    }

    public List<FieldQuery> getExcludedFields() {
        if (excludedFields == null) {
            excludedFields = new ArrayList<>();
        }

        return excludedFields;
    }

    public List<RangeQuery> getRanges() {

        if (ranges == null) {
            ranges = new ArrayList<>();
        }

        return ranges;
    }

    public PageConfig getPageConfig() {
        return pageConfig;
    }

    public void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
    }

    public SortConfig getSortConfig() {
        return sortConfig;
    }

    public void setSortConfig(SortConfig sortConfig) {
        this.sortConfig = sortConfig;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
