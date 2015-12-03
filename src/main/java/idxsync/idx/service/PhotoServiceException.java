package idxsync.idx.service;

public class PhotoServiceException extends RuntimeException {

    public PhotoServiceException(String s) {
        super(s);
    }

    public PhotoServiceException(Throwable t) {
        super(t);
    }

    public PhotoServiceException(String s, Throwable t) {
        super(s, t);
    }
}
