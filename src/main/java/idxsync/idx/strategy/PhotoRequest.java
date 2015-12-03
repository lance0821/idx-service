package idxsync.idx.strategy;

import idxsync.idx.service.PhotoSize;

public class PhotoRequest {

    private Long matrixUniqueId;
    private PhotoSize photoSize;

    public Long getMatrixUniqueId() {
        return matrixUniqueId;
    }

    public void setMatrixUniqueId(Long matrixUniqueId) {
        this.matrixUniqueId = matrixUniqueId;
    }

    public PhotoSize getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(PhotoSize photoSize) {
        this.photoSize = photoSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (obj == null) return false;

        PhotoRequest photoRequest = (PhotoRequest) obj;

        return (this.getMatrixUniqueId() != null &&
                    this.getMatrixUniqueId().equals(photoRequest.getMatrixUniqueId())) &&
                (this.getPhotoSize() != null &&
                        this.getPhotoSize().getSize().equals(photoRequest.getPhotoSize().getSize()));
    }
}
