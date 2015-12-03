package idxsync.idx.strategy;

import org.springframework.http.MediaType;

public class Utils {

    public static String getPhotoExt(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
                return ".jpg";
            case "image/bmp":
                return ".bmp";
            case "image/gif":
                return ".gif";
            case "image/png":
                return ".png";
            case "image/tiff":
                return ".tiff";
            default:
                return ".UNK";
        }
    }

    public static String getPhotoMimeType(String fileExt) {
        switch(fileExt.toLowerCase()) {
            case ".jpg":
                return "image/jpg";
            case ".bmp":
                return "image/bmp";
            case ".gif":
                return "image/gif";
            case "png":
                return "image/png";
            case "tiff":
                return "image/tiff";
            default:
                return "UNK";
        }
    }

    public static MediaType getMediaType(String mimeType) {
        switch (mimeType.toLowerCase()) {
            case "image/jpg":
            case "image/jpeg":
                return MediaType.IMAGE_JPEG;
            case "image/gif":
                return MediaType.IMAGE_GIF;
            case "image/png":
                return MediaType.IMAGE_PNG;
            case "image/bmp":
            case "image/tiff":
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
