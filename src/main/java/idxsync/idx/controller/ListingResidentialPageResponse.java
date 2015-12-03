package idxsync.idx.controller;

import idxsync.domain.ListingResidential;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListingResidentialPageResponse {

    private List<ListingResidential> listings;
    private Map<String, Object> meta;

    public List<ListingResidential> getListings() {
        return listings;
    }

    public void setListings(List<ListingResidential> listings) {
        this.listings = listings;
    }

    public Map<String, Object> getMeta() {

        if (meta == null) {
            meta = new LinkedHashMap<>();
        }

        return meta;
    }
}
