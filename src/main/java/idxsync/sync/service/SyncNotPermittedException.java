package idxsync.sync.service;

public class SyncNotPermittedException extends RuntimeException {

    public SyncNotPermittedException(String s) {
        super(s);
    }

    public SyncNotPermittedException(Throwable t) {
        super(t);
    }

    public SyncNotPermittedException(String s, Throwable t) {
        super(s, t);
    }
}
