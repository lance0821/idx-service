package idxsync.idx.service;

import idxsync.domain.*;
import idxsync.idx.strategy.PhotoData;
import idxsync.idx.strategy.PhotoStrategy;
import idxsync.persistence.repository.ListingCommercialRepository;
import idxsync.persistence.repository.ListingLandRepository;
import idxsync.persistence.repository.ListingMultRepository;
import idxsync.persistence.repository.ListingResidentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static idxsync.AppUtils.getTime;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoStrategy photoStrategy;

    @Resource
    private ListingResidentialRepository listingResidentialRepository;
    @Resource
    private ListingCommercialRepository listingCommercialRepository;
    @Resource
    private ListingLandRepository listingLandRepository;
    @Resource
    private ListingMultRepository listingMultRepository;

    private static final String LISTING_NOT_FOUND = "Listing %s not found.";
    private static final String LISTING_NO_PHOTOS = "No photos available for listing %s (mls number).";

    @Override
    public Iterable<ListingResidential> getResidentialListingsOnAndAfter(LocalDateTime matrixModifiedDate) {
        return listingResidentialRepository.findAllByMatrixModifiedDateGreaterThanEqual(getTime(matrixModifiedDate));
    }

    @Override
    public PhotoData getListingResidentialPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize) {
        ListingResidential listing = listingResidentialRepository.findByMatrixUniqueId(matrixUniqueId);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+matrixUniqueId));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingResidentialPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize) {
        ListingResidential listing = listingResidentialRepository.findByMlsNumber(mlsNumber);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+mlsNumber));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingCommercialPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize) {
        ListingCommercial listing = listingCommercialRepository.findByMatrixUniqueId(matrixUniqueId);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+matrixUniqueId));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingCommercialPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize) {
        ListingCommercial listing = listingCommercialRepository.findByMlsNumber(mlsNumber);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+mlsNumber));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingLandPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize) {
        ListingLand listing = listingLandRepository.findByMatrixUniqueId(matrixUniqueId);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+matrixUniqueId));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingLandPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize) {
        ListingLand listing = listingLandRepository.findByMlsNumber(mlsNumber);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, mlsNumber));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingMultPhoto(Long matrixUniqueId, Integer photoIndex, PhotoSize photoSize) {
        ListingMult listing = listingMultRepository.findByMatrixUniqueId(matrixUniqueId);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, ""+matrixUniqueId));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    @Override
    public PhotoData getListingMultPhoto(String mlsNumber, Integer photoIndex, PhotoSize photoSize) {
        ListingMult listing = listingMultRepository.findByMlsNumber(mlsNumber);

        if (listing == null) {
            throw new ResourceNotFoundException(String.format(LISTING_NOT_FOUND, mlsNumber));
        }

        return getListingPhoto(listing, photoIndex, photoSize);
    }

    private PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize) {

        if (idxDomain.getPhotoCount() < 1) {
            throw new ResourceNotFoundException(String.format(LISTING_NO_PHOTOS, idxDomain.getMlsNumber()));
        }

        return photoStrategy.getListingPhoto(idxDomain, photoIndex, photoSize);
    }
}
