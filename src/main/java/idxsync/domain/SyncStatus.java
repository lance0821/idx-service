package idxsync.domain;


public enum SyncStatus {
    NOT_STARTED("NOT_STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    ERROR("ERROR"),
    PARTIAL_ERROR("PARTIAL_ERROR"),
    COMPLETE("COMPLETE");

    private String status;

    SyncStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return status;
    }
}

