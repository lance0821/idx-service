package idxsync.idx.service;

import idxsync.domain.ListingResidential;
import idxsync.idx.strategy.PhotoData;

import java.time.LocalDateTime;

public interface PhotoService {
    Iterable<ListingResidential> getResidentialListingsOnAndAfter(LocalDateTime matrixModifiedDate);

    PhotoData getListingResidentialPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingResidentialPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingCommercialPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingCommercialPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingLandPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingLandPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingMultPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize);

    PhotoData getListingMultPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize);
}
