package idxsync.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.UUID;

import static idxsync.domain.SyncStatus.NOT_STARTED;

public abstract class AbstractSyncStat {

    @Id
    private String id;

    private Date created;
    private Date updated;
    protected Date syncStartTime;
    protected Date syncEndTime;

    protected long syncDurationMillis;

    protected SyncStatus status;
    protected String errorMessage;

    public AbstractSyncStat() {
        super();

        id = UUID.randomUUID().toString();

        //instantiating time values so they are never null (client is expected to set these values)
        syncStartTime = new Date();
        syncEndTime = new Date();

        status = NOT_STARTED;
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

    private long calculateSyncRunDuration() {
        return syncEndTime.getTime() - syncStartTime.getTime();
    }

    public Date getSyncStartTime() {
        return syncStartTime;
    }

    public void setSyncStartTime(Date syncStartTime) {
        this.syncStartTime = syncStartTime;
    }

    public Date getSyncEndTime() {
        return syncEndTime;
    }

    public void setSyncEndTime(Date syncEndTime) {
        this.syncEndTime = syncEndTime;

        this.syncDurationMillis = calculateSyncRunDuration();
    }

    public long getSyncDurationMillis() {
        return syncDurationMillis;
    }

    public SyncStatus getStatus() {
        return status;
    }

    public void setStatus(SyncStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
