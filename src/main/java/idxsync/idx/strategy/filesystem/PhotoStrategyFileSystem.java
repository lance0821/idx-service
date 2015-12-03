package idxsync.idx.strategy.filesystem;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import idxsync.domain.IdxDomain;
import idxsync.idx.service.PhotoServiceException;
import idxsync.idx.service.PhotoSize;
import idxsync.idx.service.ResourceNotFoundException;
import idxsync.idx.strategy.PhotoData;
import idxsync.idx.strategy.PhotoRequest;
import idxsync.idx.strategy.PhotoStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

import static idxsync.idx.strategy.Utils.getPhotoMimeType;

@Component(value= "photoStrategyFileSystem")
public class PhotoStrategyFileSystem implements PhotoStrategy, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(PhotoStrategyFileSystem.class);

    @Value("${photos.storage.path}")
    private String photosStoragePath;

    @Value("${photos.fetch.workers}")
    private int numQueueWorkers;

    @Value("${photos.retry.count}")
    private int photoRetryCount;

    @Value("${photos.retry.waitTimeSec}")
    private int photoRetryWaitTimeSec;

    private BeanFactory beanFactory;

    private LinkedBlockingDeque<PhotoRequest> photoQueue;

    private ExecutorService executorService;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private PhotoQueueHandler createPhotoQueueHandler() {
        return (PhotoQueueHandler) beanFactory.getBean("photoQueueHandler", photoQueue);
    }

    @PostConstruct
    private void initPhotoQueue() {
        if (photoQueue != null) return;

        logger.info("Initializing photo queue");

        photoQueue = new LinkedBlockingDeque<>();

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("IdxSync-photoqueue-%d")
                .setDaemon(false)
                .build();

        if (numQueueWorkers < 1) {
            throw new RuntimeException("Number of queue works must be greater than 0");
        }

        executorService = Executors.newFixedThreadPool(numQueueWorkers, threadFactory);

        for(int i = 0; i < numQueueWorkers; i++) {
            executorService.submit(createPhotoQueueHandler());
        }
    }

    @Override
    public PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize) {
        return getListingPhoto(idxDomain, photoIndex, photoSize, photoRetryCount, photoRetryWaitTimeSec);
    }

    @Override
    public PhotoData getListingPhoto(IdxDomain idxDomain, Integer photoIndex,
                                     PhotoSize photoSize, int retryCount, int retryWaitTimeSec) {
        if (logger.isDebugEnabled())
            logger.debug("requesting photo mlsNumber: {}, index: {}, size: {}",
                    idxDomain.getMlsNumber(), photoIndex, photoSize.getSize());

        //if requested file exists, return new file output stream
        PhotoData photoData = getPhotoData(idxDomain, photoIndex, photoSize);

        if (photoData != null) {
            return photoData;
        }

        //send request to photo queue for processing
        photoData = photoSyncRequest(idxDomain, photoIndex, photoSize, retryCount, retryWaitTimeSec);

        if (photoData == null) {
            //throw resource not found exception if photo data is not available
            throw new ResourceNotFoundException(
                    String.format("Requested photo cannot be found. Listing MLS number %s and photo index %d",
                            idxDomain.getMlsNumber(), photoIndex));
        }

        return photoData;
    }

    @Override
    public boolean syncPhotos(IdxDomain idxDomain, PhotoSize photoSize, int retryCount, int retryWaitTimeSec) {

        PhotoData photoData = null;
        File photoFile = null;
        boolean resp = false;

        try {
            if (logger.isDebugEnabled())
                logger.debug("Syncing photo set (mui: {}, size: {})", idxDomain.getMatrixModifiedDate(), photoSize.getSize());

            photoFile = readPhotoFile(idxDomain, 0, photoSize);

            if (photoFile != null) {
                resp = true;
            }
            else {
                photoData = photoSyncRequest(idxDomain, 0, photoSize, retryCount, retryWaitTimeSec);

                resp = photoData != null;
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            photoFile = null;

            if (photoData != null && photoData.getPhotoInputStream() != null) {
                try {
                    photoData.getPhotoInputStream().close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }

                photoData.setPhotoInputStream(null);
            }

            photoData = null;
        }

        return resp;
    }

    private boolean addToPhotoQueue(IdxDomain idxDomain, PhotoSize photoSize) {
        //only queue photo sync if id is not already on queue
        PhotoRequest req = new PhotoRequest();
        req.setMatrixUniqueId(idxDomain.getMatrixUniqueId());
        req.setPhotoSize(photoSize);

        if (!photoQueue.contains(req)) {
            return photoQueue.add(req);
        }

        return false;
    }

    private PhotoData photoSyncRequest(IdxDomain idxDomain, Integer photoIndex,
                                       PhotoSize photoSize, int retryCount, int retryWaitTimeSec) {

        //send request to photo queue for processing
        addToPhotoQueue(idxDomain, photoSize);

        //ensure photo is sync'd
        try {

            final int waitTimeoutMS = retryWaitTimeSec * 1000;
            int i = 0;

            do {
                //wait for photos to be downloaded
                if (logger.isDebugEnabled())
                    logger.debug("Waiting {} ms for photo to be downloaded...", waitTimeoutMS);

                Thread.sleep(waitTimeoutMS);

                //then return requested file output stream
                PhotoData photoData = getPhotoData(idxDomain, photoIndex, photoSize);

                //return photo data if it is available, otherwise wait for queue and retry
                if (photoData != null) {
                    return photoData;
                }

                i++;

                if (i <= retryCount) {
                    if (logger.isDebugEnabled())
                        logger.debug("Photo not ready yet, retrying...(retryCount: {})", i);
                }
                else logger.info("Photo could not be retrieved: {}, {}, {}.", idxDomain.getMatrixUniqueId(), photoIndex, photoSize.getSize());
            }
            while(i < retryCount);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    @Override
    public void deletePhotos(List<IdxDomain> entities2Delete) {
        String storagePath = photosStoragePath;

        if (storagePath == null) return;

        if (!photosStoragePath.endsWith(File.separator)) {
            storagePath += File.separator;
        }

        for(IdxDomain domain : entities2Delete) {

            File domainPhotosDir = new File(String.format("%s/%s", storagePath, domain.getMatrixUniqueId()));
            try {
                if (domainPhotosDir.exists()) {
                    FileSystemUtils.deleteRecursively(domainPhotosDir);
                    logger.info("Successfully deleted photos for listing mui: {}", domain.getMatrixUniqueId());
                }
            } catch (Exception e) {
                logger.info("An error occurred while attempting to delete files associated with mui: {}",
                        domain.getMatrixUniqueId());
            }
        }
    }

    private File readPhotoFile(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize) {
        Long matrixUniqueId = idxDomain.getMatrixUniqueId();

        String storagePath = photosStoragePath;

        if (!photosStoragePath.endsWith(File.separator)) {
            storagePath += File.separator;
        }

        String path = String.format("%s%d%s%s", storagePath, matrixUniqueId, File.separator, photoSize.getSize());

        File filePath = new File(path);

        //return null if filePath doesn't exist
        if (!filePath.exists() || !filePath.isDirectory()) return null;

        File[] fileList = filePath.listFiles();

        //bail if directory is empty
        if (fileList == null) return null;

        File photoFile = null;

        for (File file : fileList) {
            //file matches pattern: <matrixUniqueId>_<photo_index>.<file_extension>
            if (file.getName().matches(String.format("%s_%d.([a-zA-Z]+)", matrixUniqueId, photoIndex))) {
                photoFile = file;

                Date fileLastModified = new Date();
                fileLastModified.setTime(photoFile.lastModified());

                //photo has been modified since last retrieval - fetch a fresh copy from rets server
                if (idxDomain.getPhotoModificationTimestamp() != null &&
                        fileLastModified.compareTo(idxDomain.getPhotoModificationTimestamp()) < 0) return null;

                break;
            }
        }

        return photoFile;
    }

    private PhotoData getPhotoData(IdxDomain idxDomain, Integer photoIndex, PhotoSize photoSize) {

        File photoFile = readPhotoFile(idxDomain, photoIndex, photoSize);

        //photo either is out of date or doesn't exist
        if (photoFile == null) return null;

        if (!photoFile.canRead()) {
            throw new PhotoServiceException("Service cannot read photo filePath at " + photoFile.getAbsolutePath());
        }

        try {

            PhotoData photoData = new PhotoData();
            photoData.setSize(photoFile.length());
            photoData.setPhotoInputStream(new FileInputStream(photoFile));
            String name = photoFile.getName();
            photoData.setMimeType(getPhotoMimeType(name.substring(name.lastIndexOf("."))));

            return photoData;

        } catch (FileNotFoundException e) {
            throw new PhotoServiceException(e);
        }
    }
}
