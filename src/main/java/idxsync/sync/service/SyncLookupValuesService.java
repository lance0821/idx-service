package idxsync.sync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import idxsync.domain.LookupValue;
import idxsync.domain.SyncStat;
import idxsync.domain.SyncStat.SyncStatBuilder;
import idxsync.mapping.Mapping;
import idxsync.mapping.MappingItem;
import idxsync.persistence.repository.LookupValueRepository;
import idxsync.persistence.repository.SyncStatRepository;
import idxsync.rets.RetsConnection;
import org.realtors.rets.common.metadata.types.MLookup;
import org.realtors.rets.common.metadata.types.MLookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static idxsync.domain.SyncStatus.COMPLETE;

@Service
public class SyncLookupValuesService {

    private static final Logger logger = LoggerFactory.getLogger(SyncLookupValuesService.class);

    @Value("${mappings.path}")
    private String mappingsPath;

    @Resource
    private LookupValueRepository repository;

    @Resource
    private SyncStatRepository syncStatRepository;

    public SyncStat syncLookupValues(RetsConnection retsConnection, boolean forceRefresh) {

        logger.debug("sync lookup values");

        SyncStatBuilder statBuilder = new SyncStatBuilder()
                .setSyncType(LookupValue.class.getName())
                .setStatus(COMPLETE);

        //do not sync if count is greater than 1 && force refresh is not set
        if (repository.count() > 0 && !forceRefresh) {

            return statBuilder.setNumRecordsFromRets((int)repository.count())
                    .setNumRecordsInDbPostSync((int)repository.count())
                    .setSyncEndTime(new Date())
                    .setSyncStartTime(new Date())
                    .build();
        }

        long syncStartTime = System.currentTimeMillis();

        Map<String, LookupValue> lookupValueMap = new LinkedHashMap<>();

        final String mappingPath = String.format("/mappings/%s/", mappingsPath);
        getLookupValues(mappingPath + "listing-residential.json", lookupValueMap, retsConnection);
//        getLookupValues(mappingPath + "listing-commercial.json", lookupValueMap, retsConnection);
//        getLookupValues(mappingPath + "listing-land.json", lookupValueMap, retsConnection);
//        getLookupValues(mappingPath + "listing-mult.json", lookupValueMap, retsConnection);

        saveLookupValues(lookupValueMap.values());

        SyncStat syncStat = statBuilder
                .setSyncEndTime(new Date())
                .setSyncStartTime(new Date(syncStartTime))
                .setSyncType(LookupValue.class.getName())
                .setNumRecordsFromRets((int) repository.count())
                .setNumNewRecords((int) repository.count())
                .setNumRecordsInDbPostSync((int) repository.count())
                .build();

        syncStat.beforeSave();

        syncStatRepository.save(syncStat);

        return syncStat;
    }

    private void getLookupValues(String mappingFile, Map<String, LookupValue> lookupValueMap, RetsConnection retsConnection) {

        List<MappingItem> mappings = getMappings(mappingFile);

        mappings.forEach(mappingItem -> {
            MLookup lookup = retsConnection.getLookup("Property", mappingItem.getSrcFieldName());

            if (lookup == null) return;

            List<MLookupType> mLookupTypes = Arrays.asList(lookup.getMLookupTypes());


            mLookupTypes.forEach(mLookupType -> {
                LookupValue lookupValue = new LookupValue();

                lookupValue.setMlsFieldName(mappingItem.getSrcFieldName());
                lookupValue.setStandardFieldName(mappingItem.getStandardFieldName());

                lookupValue.setValueShortName(mLookupType.getShortValue());
                lookupValue.setValueLongName(mLookupType.getLongValue());

                String lookupValueKey = mappingItem.getSrcFieldName() + "_" + mLookupType.getShortValue();
                if (!lookupValueMap.containsKey(lookupValueKey)) {
                    lookupValueMap.put(lookupValueKey, lookupValue);
                }
            });
        });
    }

    private List<MappingItem> getMappings(String mappingFile) {
        InputStream mappingData = SyncLookupValuesService.class.getResourceAsStream(mappingFile);

        ObjectMapper objectMapper = new ObjectMapper();

        List<MappingItem> mappings = null;

        try {
            mappings = objectMapper.readValue(mappingData, Mapping.class).getMapping();
        } catch (IOException e) {
            throw new SyncServiceException(e);
        }

        return mappings;
    }

    private void saveLookupValues(Collection<LookupValue> lookupValues) {
        repository.deleteAll();

        repository.save(lookupValues);
    }

}
