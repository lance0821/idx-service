package idxsync.idx.service;

public enum PhotoSize {
    LARGE("large"),
    SMALL("small");

    private String size;

    PhotoSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static PhotoSize getPhotoSize(String size) {
        if (LARGE.getSize().equalsIgnoreCase(size)) return LARGE;
        else if (SMALL.getSize().equalsIgnoreCase(size)) return SMALL;

        return null;
    }
}
