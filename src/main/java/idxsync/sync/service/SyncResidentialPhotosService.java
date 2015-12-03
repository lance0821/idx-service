package idxsync.sync.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import idxsync.domain.ListingResidential;
import idxsync.domain.SyncStat;
import idxsync.idx.service.PhotoSize;
import idxsync.idx.strategy.PhotoData;
import idxsync.idx.strategy.PhotoStrategy;
import idxsync.persistence.repository.SyncStatRepository;
import idxsync.rets.RetsConnection;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static idxsync.AppUtils.toDate;
import static idxsync.domain.SyncStatus.COMPLETE;
import static idxsync.domain.SyncStatus.ERROR;

@Service
public class SyncResidentialPhotosService implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncResidentialPhotosService.class);

    @Autowired
    private PhotoStrategy photoStrategy;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private SyncStatRepository syncStatRepository;

    private static final String THREAD_PROCESSING_ERROR = "An error occurred while processing one of the sync threads.";
    private static final String THREAD_FUTURE_ERROR =
            "Sync callable thread did not complete successfully future.isDone(): %s future.isCancelled(): %s.";

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("IdxSync-%d")
            .setDaemon(false)
            .build();

    final int PAGE_REQUEST_SIZE = 50;

    @Override
    public SyncStat sync(RetsConnection retsConnection, LocalDateTime syncDateTime) {

        logger.info("Sync Residential Photos from {}", syncDateTime.toString());

        long syncStartTime = System.currentTimeMillis();

        SyncStat syncStat = new SyncStat(PhotoData.class.getTypeName());
        syncStat.setStatus(COMPLETE);

        FacetedPage<ListingResidential> listingPage = fetchListings(syncDateTime, new PageRequest(0, PAGE_REQUEST_SIZE));

        logger.info("Number listings with photos to sync: {}", listingPage.getNumberOfElements());

        int listingCount = 0;
        while(listingPage != null) {

            long startTime = System.currentTimeMillis();

            logger.info("Fetching batch {} of {}, size of batch: {}", listingPage.getNumber(), listingPage.getTotalPages(), PAGE_REQUEST_SIZE);
            listingCount += syncPhotoBatch(listingPage.getContent(), syncStat);

            long syncTime = System.currentTimeMillis() - startTime;

            logger.info("Sync of batch {} took {}ms", listingPage.getNumber(), syncTime);

            if (listingPage.getNumber() >= listingPage.getTotalPages()) {
                listingPage = null;
            }
            else {
                listingPage = fetchListings(syncDateTime, new PageRequest(listingPage.getNumber() + 1, PAGE_REQUEST_SIZE));
            }
        }

        long syncEndTime = System.currentTimeMillis();

        syncStat.setSyncStartTime(new Date(syncStartTime));
        syncStat.setSyncEndTime(new Date(syncEndTime));
        syncStat.setNumNewRecords(listingCount);
        syncStat.setNumRecordsFromRets(listingCount);
        syncStat.setNumUpdatedRecords(listingCount);

        syncStat.beforeSave();

        syncStatRepository.save(syncStat);

        return syncStat;
    }

    private FacetedPage<ListingResidential> fetchListings(LocalDateTime syncDateTime, PageRequest pageRequest) {
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.must(
                new RangeQueryBuilder("matrixModifiedDate")
                        .gte(toDate(syncDateTime).getTime()));

        boolQueryBuilder.must(
                new RangeQueryBuilder("photoCount")
                        .gt(0));

        NativeSearchQuery searchQuery = searchQueryBuilder
                .withIndices("idx")
                .withTypes("residential")
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();

        return elasticsearchTemplate.queryForPage(searchQuery, ListingResidential.class);
    }

    private int syncPhotoBatch(List<ListingResidential> listings, SyncStat syncStat) {

        int syncdListingsWithPhotosCount = 0;

        ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);

        List<Future<Boolean>> futures = setupSyncPhotoCallables(executorService, listings);

        executorService.shutdown();

        final int syncTimeoutMinutes = 10;

        try {
            if (!executorService.awaitTermination(syncTimeoutMinutes, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new SyncServiceException(e);
        }

        logger.info("Processing photo callables...");

        for (Future<Boolean> future : futures) {
            Boolean syncResult = null;

            try {
                if (future.isDone() && !future.isCancelled()) {
                    syncResult = future.get();
                }
            } catch (Exception e) {
                logger.warn(THREAD_PROCESSING_ERROR, e);
            } finally {
                //Something went wrong if syncResult is null
                if (syncResult == null) {
                    syncStat.setStatus(ERROR);

                    String errorMsg =
                            String.format(THREAD_FUTURE_ERROR,
                                    future.isDone(), future.isCancelled());

                    logger.warn(errorMsg);

                    String prevErrMsg = syncStat.getErrorMessage();
                    if (prevErrMsg != null) {
                        errorMsg = prevErrMsg + "\n" + errorMsg;
                    }

                    syncStat.setErrorMessage(errorMsg);
                } else {
                    syncdListingsWithPhotosCount++;
                }
            }
        }

        return syncdListingsWithPhotosCount;
    }

    private List<Future<Boolean>> setupSyncPhotoCallables(ExecutorService executorService,
                                                            List<ListingResidential> listings) {

        List<Future<Boolean>> photoFutures = new LinkedList<>();
        int listingCount = 0;
        for (ListingResidential listing : listings) {

            photoFutures.add(executorService.submit(() -> syncListingPhotos(listing, PhotoSize.LARGE)));
            photoFutures.add(executorService.submit(() -> syncListingPhotos(listing, PhotoSize.SMALL)));

            listingCount++;
        }

        logger.info("Creating sync photo callables for ({}) listings...", listingCount);

        return photoFutures;
    }

    private boolean syncListingPhotos(ListingResidential listing, PhotoSize photoSize) {

        final int retryCount = 9999;
        final int retryTimeoutSec = 1;
        boolean result = false;

        try {
            result = photoStrategy.syncPhotos(listing, photoSize, retryCount, retryTimeoutSec);
        } catch (Exception e) {
            logger.info("Failed to retrieve listing residential photos: {}", e.getMessage());
        }

        return result;
    }
}
