package idxsync.idx.strategy;


import idxsync.domain.IdxDomain;
import idxsync.idx.service.PhotoSize;

import java.util.List;

public interface PhotoStrategy {

    PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize);
    PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex,
                              PhotoSize photoSize, int retryCount, int retryWaitTimeoutSec);
    void deletePhotos(List<IdxDomain> entities2Delete);
    boolean syncPhotos(IdxDomain idxDomain, PhotoSize photoSize, int retryCount, int retryWaitTimeSec);
}
