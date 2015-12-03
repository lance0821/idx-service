package idxsync.rets;

public class RetsConnectionException extends RuntimeException {

    public RetsConnectionException(String s) {
        super(s);
    }

    public RetsConnectionException(Throwable th) {
        super(th);
    }
}
