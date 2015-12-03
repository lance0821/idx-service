package idxsync.idx.strategy.filesystem;

import com.google.common.io.ByteStreams;
import idxsync.idx.service.PhotoServiceException;
import idxsync.idx.service.PhotoSize;
import idxsync.idx.strategy.PhotoData;
import idxsync.idx.strategy.PhotoRequest;
import idxsync.rets.RetsConnection;
import org.realtors.rets.client.GetObjectIterator;
import org.realtors.rets.client.GetObjectResponse;
import org.realtors.rets.client.RetsException;
import org.realtors.rets.client.SingleObjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import static idxsync.idx.strategy.Utils.getPhotoExt;

@Component
@Scope(value = "prototype")
public class PhotoQueueHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PhotoQueueHandler.class);

    @Value("${photos.storage.path}")
    private String photosStoragePath;

    @Autowired
    private RetsConnection retsConnection;

    private LinkedBlockingDeque<PhotoRequest> photoDeque;

    public PhotoQueueHandler(LinkedBlockingDeque<PhotoRequest> photoDeque) {

        this.photoDeque = photoDeque;
    }

    private final static int SLEEP_TIME_QUEUE_CHECK = 100;
    private final static int SLEEP_TIME_AFTER_PHOTO_FETCH = 50;

    @Override
    public void run() {
        logger.info("Starting photo request processing queue thread.");

        while (true) {
            if (!photoDeque.isEmpty()) {
                PhotoRequest req = photoDeque.pop();

                long starTime = System.currentTimeMillis();

                if (logger.isDebugEnabled())
                    logger.debug("Processing request from photo queue (mui:{}, photoSize: {})",
                            req.getMatrixUniqueId(), req.getPhotoSize().getSize());

                fetchListingPhotos(retsConnection, req.getMatrixUniqueId(), req.getPhotoSize());

                long totalTime = System.currentTimeMillis() - starTime;

                if (logger.isDebugEnabled())
                    logger.debug("Took a total of {}ms to retrieve and save (mui:{}, photoSize: {})",
                            totalTime, req.getMatrixUniqueId(), req.getPhotoSize().getSize());

                req = null;
                System.gc();
                //sleep between rets photo calls
                sleep(SLEEP_TIME_AFTER_PHOTO_FETCH);


            }
            else sleep(SLEEP_TIME_QUEUE_CHECK);
        }
    }

    private void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new PhotoServiceException(e);
        }
    }

    private Set<PhotoData> fetchListingPhotos(RetsConnection retsConnection,
                                              Long matrixUniqueId, PhotoSize photoSize) {

        GetObjectResponse objectResponse = null;

        long startTime = System.currentTimeMillis();

        if (photoSize == PhotoSize.LARGE) {
            objectResponse = retsConnection.getPropertyLargePhotos(
                    Collections.singletonList(matrixUniqueId));
        } else {
            objectResponse = retsConnection.getPropertySmallPhotos(
                    Collections.singletonList(matrixUniqueId));
        }

        long downloadTime = System.currentTimeMillis() - startTime;

        if (logger.isDebugEnabled())
            logger.debug("Retrieved {} photo set for listing {} ({}ms).", photoSize.getSize(), matrixUniqueId, downloadTime);

        GetObjectIterator<SingleObjectResponse> propertyPhotos = null;
        try {
            propertyPhotos = objectResponse.iterator();

            return savePhotoSet(propertyPhotos, matrixUniqueId, photoSize);
        } catch (RetsException e) {
            throw new PhotoServiceException(e);
        }
        finally {
            try {
                if (objectResponse != null && objectResponse.getInputStream() != null) {
                    objectResponse.getInputStream().close();
                }
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    private Set<PhotoData> savePhotoSet(GetObjectIterator<SingleObjectResponse> propertyPhotos, Long matrixUniqueId, PhotoSize photoSize) {

        Set<PhotoData> photoSet = new LinkedHashSet<>();

        int photoIdx = 0;

        if (logger.isDebugEnabled() && propertyPhotos.hasNext()) {
            logger.debug("Photo data set retrieved - mui: {}, size: {}", matrixUniqueId, photoSize.getSize());
        }

        while (propertyPhotos.hasNext()) {
            SingleObjectResponse photo = propertyPhotos.next();

            OutputStream outputStream = null;

            try {
                String storagePath = photosStoragePath;

                if (!photosStoragePath.endsWith("/") || !photosStoragePath.endsWith("\\")) {
                    storagePath += File.separator;
                }

                //<photo_storage_path>/<matrix_unique_id>/<photo_size>
                String photoFilePathStr = String.format("%s%d%s%s%s",
                        storagePath,
                        matrixUniqueId,
                        File.separator,
                        photoSize.getSize(),
                        File.separator);

                File photoFilePath = new File(photoFilePathStr);

                if (!photoFilePath.exists() && !photoFilePath.mkdirs()) {
                    throw new PhotoServiceException(String.format("Failed to create photo storagePath %s!", photoFilePathStr));
                }

                //image file storagePath: <photo_storage_path>/<matrix_unique_id>/<photo_size>/<mls_number>_<index>.jpg
                File photoFile = new File(photoFilePath, String.format("%d_%d%s",
                        matrixUniqueId,
                        photoIdx++,
                        getPhotoExt(photo.getType())));

                if (photoFile.exists()) {

                    if (logger.isDebugEnabled())
                        logger.debug("Photo file {} exists. Will delete it and create a new one.", photoFile.getAbsolutePath());

                    boolean isDelete = photoFile.delete();
                    if (logger.isDebugEnabled())
                        logger.debug("Result of deleting file {}: {}", photoFile.getName(), isDelete);
                }

                outputStream = new FileOutputStream(photoFile);

                byte[] photoBytes = ByteStreams.toByteArray(photo.getInputStream());

                outputStream.write(photoBytes);

                PhotoData photoData = new PhotoData();
                photoData.setMimeType(photo.getType());
                photoData.setSize(photoBytes.length);

                photoSet.add(photoData);

                photoFile = null;


            } catch (IOException e) {
                throw new PhotoServiceException(e);
            } finally {

                if (outputStream != null) {
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (IOException e) {
                        logger.warn("Failed to close output stream.", e);
                    }
                }

                if (photo != null && photo.getInputStream() != null) {
                    try {
                        photo.getInputStream().close();
                        photo = null;
                    } catch (IOException e) {
                        logger.warn("Failed to close photo input stream.", e);
                    }
                }
            }
        }

        return photoSet;
    }
}
