package idxsync.idx.strategy.cached;


import idxsync.domain.IdxDomain;
import idxsync.idx.service.PhotoSize;
import idxsync.idx.strategy.PhotoData;
import idxsync.idx.strategy.PhotoStrategy;
import idxsync.rets.RetsConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value= "photoStrategyCached")
public class PhotoStrategyCached implements PhotoStrategy {

    @Autowired
    private RetsConnection retsConnection;

    @Override
    public PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize, int retryCount, int retryWaitTimeoutSec) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void deletePhotos(List<IdxDomain> entities2Delete) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public boolean syncPhotos(IdxDomain idxDomain, PhotoSize photoSize, int retryCount, int retryWaitTimeSec) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
