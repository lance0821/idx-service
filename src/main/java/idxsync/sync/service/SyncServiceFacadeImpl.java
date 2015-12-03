package idxsync.sync.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import idxsync.domain.*;
import idxsync.domain.SyncStat.SyncStatBuilder;
import idxsync.persistence.repository.SyncFullStatRepository;
import idxsync.rets.RetsConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static idxsync.AppUtils.toLocalDateTime;
import static idxsync.domain.SyncStatus.*;

@Service
public class SyncServiceFacadeImpl implements SyncServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceFacadeImpl.class);

    @Value("${sync.worker.threads.timeout.minutes}")
    private int syncTimeoutMinutes;

    @Value("${photos.fetch.residential.sync}")
    private boolean syncResidentialPhotos;

    @Autowired
    private SyncListingResidentialService syncListingResidentialService;
    @Autowired
    private SyncListingCommercialService syncListingCommercialService;
    @Autowired
    private SyncListingLandService syncListingLandService;
    @Autowired
    private SyncListingMultService syncListingMultService;
    @Autowired
    private SyncOpenHouseService syncOpenHouseService;
    @Autowired
    private SyncLookupValuesService syncLookupValuesService;
    @Autowired
    private SyncResidentialPhotosService syncResidentialPhotosService;
    @Autowired
    private RetsConnection retsConnection;
    @Resource
    private SyncFullStatRepository syncFullStatRepository;

    private static final String THREAD_PROCESSING_ERROR = "An error occurred while processing one of the sync threads.";
    private static final String THREAD_FUTURE_ERROR =
            "Sync callable thread did not complete successfully future.isDone(): %s future.isCancelled(): %s.";

    private final String keyResidential = "resi";
    private final String keyCommercial = "comm";
    private final String keyLand = "land";
    private final String keyMult = "mult";
    private final String keyOpenHouse = "openhouse";
    private final String keyStashSearchTerms = "stashSearchTerms";
    private final String keyLookupValues = "lookupValues";

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("IdxSync-%d")
            .setDaemon(false)
            .build();

    @Override
    public SyncFullStat syncIdxData(String syncToken) {
        return syncIdxData(syncToken, LocalDateTime.now().minusYears(50));
    }

    @Override
    public SyncFullStat syncIdxData(String syncToken, LocalDateTime initialSyncDate) {

        LocalDateTime syncDateTime;

        //find most recent sync timestamp
        SyncFullStat mostRecentSync = null;
        if (syncFullStatRepository.count() > 0) {
            Iterable<SyncFullStat> stats = syncFullStatRepository.findAllByStatusOrderByUpdatedDesc(COMPLETE);
            Iterator<SyncFullStat> iterator = stats.iterator();
            if (iterator.hasNext()) {
                mostRecentSync = iterator.next();
            }
        }

        //if no most recent sync found, assume initial sync
        if (mostRecentSync == null) {
            logger.info("No most recent timestamp found. Assuming initial sync.");
            syncDateTime = initialSyncDate;
        }
        //utilize most recent sync timestamp
        else {
            syncDateTime = toLocalDateTime(mostRecentSync.getSyncEndTime());
            logger.info(String.format("Syncing from most recent: %s",
                    syncDateTime.format(DateTimeFormatter.ISO_DATE_TIME)));
        }

        SyncFullStat syncFullStat = new SyncFullStat();
        syncFullStat.setSyncToken(syncToken);

        setSyncFullStatus(syncFullStat, IN_PROGRESS);

        Set<SyncStat> syncStatList = null;

        //sync idx
        try {
            syncStatList = syncAllData(syncDateTime);
            syncFullStat.setSyncEndTime(new Date());
            syncFullStat.getSyncStatList().addAll(syncStatList);
            syncStatList.forEach(stat -> {


                if (stat.getStatus() == ERROR) {
                    setSyncFullStatus(syncFullStat, ERROR,
                            "There were sync failures. See sync stat batch data for details.");
                }
            });

            if (syncFullStat.getStatus() != ERROR) {
                setSyncFullStatus(syncFullStat, COMPLETE);
            }

        } catch (Exception e) {
            logger.error("An error occurred during all listings sync operation: {}", e);

            setSyncFullStatus(syncFullStat, ERROR,
                    String.format("An error occurred during all listings sync operation: %s", e.getMessage()));
        } finally {

            logger.info("SyncFullStat:\n\n{}", syncFullStat.toString());
        }

        return syncFullStat;
    }

    private Set<SyncStat> syncAllData(LocalDateTime syncFromDateTime) {

        Set<SyncStat> listingStats = syncListingData(syncFromDateTime);
        Set<SyncStat> metaStats = syncIdxMetaData();

        SyncStat photoStats = null;

        if (syncResidentialPhotos) {
            photoStats = syncResidentialPhotos(syncFromDateTime);
        }

        Set<SyncStat> allSyncStats = new HashSet<>();

        allSyncStats.addAll(listingStats);
        allSyncStats.addAll(metaStats);

        if (photoStats != null) {
            allSyncStats.add(photoStats);
        }

        return allSyncStats;
    }

    private Set<SyncStat> syncListingData(LocalDateTime syncFromDateTime) {

        Set<SyncStat> listingStats = new HashSet<>();

        ExecutorService executorService = Executors.newFixedThreadPool(5, threadFactory);

        Map<String, Future<SyncStat>> futuresMap =
                setupSyncListingsCallables(executorService, syncFromDateTime);

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(syncTimeoutMinutes, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new SyncServiceException(e);
        }

        futuresMap.forEach((futureKey, future) -> {
            SyncStat syncStat = null;
            try {
                if (future.isDone() && !future.isCancelled()) {
                    syncStat = future.get();
                }
            } catch (Exception e) {
                logger.warn(THREAD_PROCESSING_ERROR, e);
            } finally {
                //Something went wrong if syncStat is null
                if (syncStat == null) {
                    SyncStatBuilder statBuilder =
                            new SyncStatBuilder()
                            .setStatus(ERROR);

                    String errorMsg =
                            String.format(THREAD_FUTURE_ERROR,
                                    future.isDone(), future.isCancelled());

                    logger.warn(errorMsg);
                    statBuilder.setErrorMessage(errorMsg);

                    switch (futureKey) {
                        case keyResidential:
                            statBuilder.setSyncType(ListingResidential.class.getName());
                            break;
                        case keyCommercial:
                            statBuilder.setSyncType(ListingCommercial.class.getName());
                            break;
                        case keyLand:
                            statBuilder.setSyncType(ListingLand.class.getName());
                            break;
                        case keyMult:
                            statBuilder.setSyncType(ListingMult.class.getName());
                            break;
                        case keyOpenHouse:
                            statBuilder.setSyncType(OpenHouse.class.getName());
                            break;
                    }

                    syncStat = statBuilder.build();
                }

                listingStats.add(syncStat);
            }
        });

        return listingStats;
    }

    private Set<SyncStat> syncIdxMetaData() {

        Set<SyncStat> metaDataStats = new HashSet<>();

        //sync metadata
        ExecutorService executorService = Executors.newFixedThreadPool(2, threadFactory);

        Map<String, Future<SyncStat>> futuresMap = setupMetadataCallables(executorService);

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(syncTimeoutMinutes, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new SyncServiceException(e);
        }

        futuresMap.forEach((futureKey, future) -> {
            SyncStat syncStat = null;
            try {
                if (future.isDone() && !future.isCancelled()) {
                    syncStat = future.get();
                }
            } catch (Exception e) {
                logger.warn(THREAD_PROCESSING_ERROR, e);
            } finally {
                //Something went wrong if syncStat is null
                if (syncStat == null) {
                    SyncStatBuilder statBuilder = new SyncStatBuilder()
                            .setStatus(ERROR);

                    String errorMsg =
                            String.format(THREAD_FUTURE_ERROR,
                                    future.isDone(), future.isCancelled());

                    logger.warn(errorMsg);
                    statBuilder.setErrorMessage(errorMsg);

                    switch (futureKey) {
                        case keyStashSearchTerms:
                            statBuilder.setSyncType(SearchTerm.class.getName());
                            break;
                        case keyLookupValues:
                            statBuilder.setSyncType(LookupValue.class.getName());
                            break;
                    }

                    syncStat = statBuilder.build();
                }

                metaDataStats.add(syncStat);
            }
        });

        return metaDataStats;
    }

    private SyncStat syncResidentialPhotos(LocalDateTime syncFromDateTime) {

        return syncResidentialPhotosService.sync(retsConnection, syncFromDateTime);
    }

    private Map<String, Future<SyncStat>> setupSyncListingsCallables(
            ExecutorService executorService, LocalDateTime syncFromDateTime) {

        Map<String, Future<SyncStat>> futuresMap = new LinkedHashMap<>();

        futuresMap.put(keyResidential,
                executorService.submit(() -> syncListingResidentialService.sync(retsConnection, syncFromDateTime)));

        futuresMap.put(keyCommercial,
                executorService.submit(() -> syncListingCommercialService.sync(retsConnection, syncFromDateTime)));

        futuresMap.put(keyLand,
                executorService.submit(() -> syncListingLandService.sync(retsConnection, syncFromDateTime)));


        futuresMap.put(keyMult,
                executorService.submit(() -> syncListingMultService.sync(retsConnection, syncFromDateTime)));

        futuresMap.put(keyOpenHouse,
                executorService.submit(() -> syncOpenHouseService.sync(retsConnection, syncFromDateTime)));

        return futuresMap;
    }

    private Map<String, Future<SyncStat>> setupMetadataCallables(ExecutorService executorService) {
        Map<String, Future<SyncStat>> futuresMap = new LinkedHashMap<>();

        futuresMap.put(keyStashSearchTerms,
                executorService.submit(syncListingResidentialService::stashSearchTerms));

        futuresMap.put(keyLookupValues,
                executorService.submit(() -> syncLookupValuesService.syncLookupValues(retsConnection, false)));

        return futuresMap;
    }

    private void setSyncFullStatus(SyncFullStat syncFullStat, SyncStatus status, String... options) {

        String errorMsg = null;
        if (options != null && options.length > 0) {
            errorMsg = options[0];
        }

        syncFullStat.setStatus(status);

        if (errorMsg != null) {
            syncFullStat.setErrorMessage(errorMsg);
        }

        syncFullStat.beforeSave();

        syncFullStatRepository.save(syncFullStat);
    }
}
