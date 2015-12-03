package idxsync.idx.service;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String s) {
        super(s);
    }

    public ResourceNotFoundException(Throwable t) {
        super(t);
    }

    public ResourceNotFoundException(String s, Throwable t) {
        super(s, t);
    }
}
