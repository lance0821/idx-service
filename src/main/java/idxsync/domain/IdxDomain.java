package idxsync.domain;

import java.util.Date;
import java.util.UUID;

public abstract class IdxDomain {

    private String id;
    private Date created;
    private Date updated;

    public IdxDomain() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void beforeSave() {
        if (created == null) {
            created = updated = new Date();
        }
        else updated = new Date();
    }

    public abstract Date getMatrixModifiedDate();

    public abstract void setMatrixModifiedDate(Date matrixModifiedDate);

    public abstract long getMatrixUniqueId();

    public abstract void setMatrixUniqueId(long matrixUniqueId);

    public abstract String getMlsNumber();

    public abstract void setMlsNumber(String mlsNumber);

    public abstract int getPhotoCount();

    public abstract void setPhotoCount(int photoCount);

    public abstract Date getPhotoModificationTimestamp();

    public abstract void setPhotoModificationTimestamp(Date photoModificationTimestamp);
}
