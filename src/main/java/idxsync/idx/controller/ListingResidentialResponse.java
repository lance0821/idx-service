package idxsync.idx.controller;

import idxsync.domain.ListingResidential;

public class ListingResidentialResponse {
    private ListingResidential listing;

    public ListingResidential getListing() {
        return listing;
    }

    public void setListing(ListingResidential listing) {
        this.listing = listing;
    }
}
