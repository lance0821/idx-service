package idxsync.idx.strategy;


import java.io.InputStream;

public class PhotoData {

    private String mimeType;
    private long size;
    private InputStream photoInputStream;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public InputStream getPhotoInputStream() {
        return photoInputStream;
    }

    public void setPhotoInputStream(InputStream photoInputStream) {
        this.photoInputStream = photoInputStream;
    }
}
