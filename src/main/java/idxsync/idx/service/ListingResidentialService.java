package idxsync.idx.service;

import idxsync.domain.ListingResidential;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ListingResidentialService {
    ListingResidential getListing(String mlsNumber);

    ListingResidential getListing(Long matrixUniqueId);

    Page<ListingResidential> getListings(ListingsRequest listingsRequest);

    void transformLookupValues(List<ListingResidential> listings);

    void transformLookupValues(ListingResidential listingResidential);
}
