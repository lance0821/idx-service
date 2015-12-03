package idxsync.sync.strategy;

import idxsync.domain.*;
import idxsync.idx.strategy.PhotoStrategy;
import idxsync.persistence.repository.SyncStatRepository;
import idxsync.rets.RetsConnection;
import idxsync.rets.RetsConnectionException;
import idxsync.sync.service.SyncServiceException;
import org.realtors.rets.client.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static idxsync.domain.SyncStatus.*;

@Component(value= "syncStrategyIdx")
public class SyncStrategyIdx implements SyncStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SyncStrategyIdx.class);

    @Value("${sync.idx.sync.strategy.retry}")
    private int maxRetry;

    @Resource
    private SyncStatRepository syncStatRepository;

    @Autowired
    private PhotoStrategy photoStrategyFileSystem;

    @Override
    public SyncStat sync(SyncStrategyConfig syncStrategyConfig) {

        SyncStat syncStat = new SyncStat(syncStrategyConfig.getDomainName());

        setSyncStatus(syncStat, IN_PROGRESS);

        Set<? extends IdxDomain> entities2Sync =
                getEntities2Sync(syncStrategyConfig, syncStat);

        //retrieve all MUIs associated with this domain (this is needed to determine which entities need to be deleted)
        Map<Long, Long> allMUIs = getAllMUIs(syncStrategyConfig);

        return sync(syncStat, entities2Sync, allMUIs, syncStrategyConfig.getDomainRepository());
    }

    private Set<? extends IdxDomain> getEntities2Sync(
            SyncStrategyConfig syncStrategyConfig,
            SyncStat syncStat) {

        //retrieve all domain records after a particular date
        List<SearchResult> searchResults =
                performSearch(
                        syncStrategyConfig.getDomainName(),
                        syncStrategyConfig.getRetsConnection(),
                        syncStrategyConfig.getSyncQuery());

        if (searchResults == null) {

            throw new SyncServiceException("Search results list should never be null");
        }

        Set<IdxDomain> domains = new HashSet<>();

        searchResults.forEach(searchResult -> {

            syncStat.setNumRecordsFromRets(syncStat.getNumRecordsFromRets() + searchResult.getCount());

            logger.info("Retrieved ({}) entities from the RETS server", searchResult.getCount());

            logger.debug("Mapping RETS search results to domain");

            domains.addAll(syncStrategyConfig.getMapper().mapData(searchResult));
        });

        return syncStrategyConfig.getMapper().mapData(searchResults);
    }

    private SyncStat sync(
            SyncStat syncStat,
            Iterable<? extends IdxDomain> entities2Sync,
            Map<Long, Long> allMUIs,
            CrudRepository domainRepository) {

        List<IdxDomain> entities2Save = new LinkedList<>();
        List<IdxDomain> entities2Delete = new LinkedList<>();

        boolean isSuccess = false;

        try {

            syncStat.setNumRecordsInDbPreSync((int) domainRepository.count());

            //grab all entities in database
            Iterable dbEntities = domainRepository.findAll();

            //dump database entities into a map for quick retrieval
            Map<Long, IdxDomain> dbEntitiesMap = new LinkedHashMap<>();

            Iterator dbIter = dbEntities.iterator();
            while(dbIter.hasNext()) {
                IdxDomain domain = (IdxDomain) dbIter.next();
                dbEntitiesMap.put(domain.getMatrixUniqueId(), domain);
            }

            //iterate over each entity from RETS server that needs to be synced
            entities2Sync.forEach(entity2Sync -> {
                IdxDomain dbEntity = dbEntitiesMap.get(entity2Sync.getMatrixUniqueId());

                //new entity2Sync?
                if (dbEntity == null) {
                    entities2Save.add(entity2Sync);

                    syncStat.incrementNumNewRecords();
                }
                //domain needs to be refreshed?
                else if (SyncUtils.domainNeedsRefresh(dbEntity, entity2Sync)) {
                    SyncUtils.refreshDomain(dbEntity, entity2Sync);

                    entities2Save.add(dbEntity);

                    syncStat.incrementNumUpdatedRecords();
                }
            });

            //figure out which records are no longer in RETS system (allMUIs) and therefore need to be deleted from DB
            dbIter = dbEntities.iterator();
            while(dbIter.hasNext()) {
                IdxDomain domain = (IdxDomain) dbIter.next();
                //if db entity is not present in allMUIs, mark for deletion
                if (allMUIs.get(domain.getMatrixUniqueId()) == null) {
                    entities2Delete.add(domain);
                }
            }

            isSuccess = true;

        } catch (Exception e) {

            String errorMsg = String.format("An error occurred during sync process: %s", e.getMessage());

            logger.error(errorMsg, e);
            setSyncStatus(syncStat, ERROR, errorMsg);
        } finally {

            persistSyncData(isSuccess, entities2Save, entities2Delete, syncStat, domainRepository);
        }

        return syncStat;
    }

    private void persistSyncData(boolean isSuccess,
                                   List<IdxDomain> entities2Save,
                                   List<IdxDomain> entities2Delete,
                                   SyncStat syncStat,
                                   CrudRepository domainRepository) {

        //only persist sync data if successful
        if (isSuccess) {
            //save new or modified entities
            logger.info("Saving new and/or updated entities ({}) to database.", entities2Save.size());

            if (entities2Save.size() > 0) {

                entities2Save.forEach(entity -> {
                    entity.beforeSave();
                });

                domainRepository.save(entities2Save);
            }

            //delete entities no longer available from RETS server
            logger.info("Deleting entities ({}) no longer available from RETS server", entities2Delete.size());
            if (entities2Delete.size() > 0)
                domainRepository.delete(entities2Delete);
            syncStat.setNumDeletedRecords(entities2Delete.size());
            photoStrategyFileSystem.deletePhotos(entities2Delete);
        }

        syncStat.setNumRecordsInDbPostSync((int) domainRepository.count());

        syncStat.setSyncEndTime(new Date());

        setSyncStatus(syncStat, COMPLETE);

        logger.info("Sync Stats:\n{}", syncStat.toString());
    }

    private List<SearchResult> performSearch(String domainName, RetsConnection retsConnection,
                                             String query, Map... options) {
        return performRetsSearch(domainName, retsConnection, query, 0, options);
    }

    private List<SearchResult> performRetsSearch(String domainName, RetsConnection retsConnection,
                                             String query, int retryCount, Map... options) {

        try {
            if (ListingCommercial.class.getName().equals(domainName)) {
                return retsConnection.searchListingCommercial(query, options);
            }
            else if (ListingLand.class.getName().equals(domainName)) {
                return retsConnection.searchListingLand(query, options);
            }
            else if (ListingMult.class.getName().equals(domainName)) {
                return retsConnection.searchListingMult(query, options);
            }
            else if (ListingResidential.class.getName().equals(domainName)) {
                return retsConnection.searchListingResidential(query, options);
            }
            else if (OpenHouse.class.getName().equals(domainName)) {
                return retsConnection.searchOpenHouse(query, options);
            }
        }
        catch(RetsConnectionException e) {

            logger.info("Failed performRetsSearch. Retrying - retryCount: {}", retryCount);

            //retry any failed searches
            List<SearchResult> searchResults = null;
            if(retryCount < maxRetry) {
                return performRetsSearch(domainName, retsConnection, query, ++retryCount, options);
            }
        }

        return null;
    }

    private Map<Long, Long> getAllMUIs(SyncStrategyConfig syncStrategyConfig) {

        Map<String, String> options = new LinkedHashMap<>();
        options.put("Select", "Matrix_Unique_ID");

        List<SearchResult> searchResults =
                performSearch(syncStrategyConfig.getDomainName(), syncStrategyConfig.getRetsConnection(),
                        syncStrategyConfig.getDeleteQuery(), options);

        if (searchResults == null) {
            throw new SyncServiceException("Search results list shouldn't be null");
        }

        Map<Long, Long> allMUIsMap = new LinkedHashMap<>();

        searchResults.forEach(searchResult -> {
            Iterator iterator = searchResult.iterator();
            while (iterator.hasNext()) {
                Long mui = Long.valueOf(((String[])iterator.next())[0]);
                allMUIsMap.put(mui, mui);
            }
        });

        return allMUIsMap;
    }

    private void setSyncStatus(SyncStat syncStat, SyncStatus syncStatus, String... options) {

        String errorMsg = null;
        if (options != null && options.length > 0) {
            errorMsg = options[0];
        }

        syncStat.setStatus(syncStatus);

        if (errorMsg != null) {
            syncStat.setErrorMessage(errorMsg);
        }

        syncStat.beforeSave();

        syncStatRepository.save(syncStat);
    }
}
