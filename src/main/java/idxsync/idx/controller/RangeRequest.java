package idxsync.idx.controller;

import idxsync.idx.service.RangeQuery;

import java.util.List;

public class RangeRequest {

    private List<RangeQuery> ranges;

    public List<RangeQuery> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeQuery> ranges) {
        this.ranges = ranges;
    }
}
