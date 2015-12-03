package idxsync.sync.service;

public class SyncServiceException extends RuntimeException {

    public SyncServiceException(String s) {
        super(s);
    }

    public SyncServiceException(Throwable t) {
        super(t);
    }

    public SyncServiceException(String s, Throwable t) {
        super(s, t);
    }
}
