package idxsync.mapping;


public class MapperException extends RuntimeException {

    public MapperException(String s) {
        super(s);
    }

    public MapperException(Exception e) {
        super(e);
    }
}
