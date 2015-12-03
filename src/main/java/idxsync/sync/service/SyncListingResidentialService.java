package idxsync.sync.service;

import idxsync.domain.*;
import idxsync.domain.SyncStat.SyncStatBuilder;
import idxsync.mapping.ListingResidentialMapper;
import idxsync.persistence.repository.ListingResidentialRepository;
import idxsync.persistence.repository.SearchTermResidentialRepository;
import idxsync.persistence.repository.SearchTermSchoolRepository;
import idxsync.persistence.repository.SyncStatRepository;
import idxsync.rets.RetsConnection;
import idxsync.sync.strategy.SyncStrategy;
import idxsync.sync.strategy.SyncStrategyConfig.SyncConfigBuilder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import static idxsync.domain.SearchTermType.*;
import static idxsync.domain.SyncStatus.COMPLETE;

@Service
public class SyncListingResidentialService implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncListingResidentialService.class);

    @Resource
    private ListingResidentialRepository repository;

    @Resource
    private SearchTermResidentialRepository searchTermResidentialRepository;

    @Resource
    private SearchTermSchoolRepository searchTermSchoolRepository;

    @Resource
    private SyncStatRepository syncStatRepository;

    @Autowired
    private ListingResidentialMapper mapper;

    @Autowired
    @Qualifier("syncStrategyIdx")
    private SyncStrategy syncStrategy;

    @Override
    public SyncStat sync(RetsConnection retsConnection, LocalDateTime syncDateTime) {
        logger.debug("Calling sync listing residential");
        return syncStrategy.sync(
                new SyncConfigBuilder()
                        .setDomainName(ListingResidential.class.getName())
                        .setRetsConnection(retsConnection)
                        .setMapper(mapper)
                        .setDomainRepository(repository)
                        .setSyncQuery(String.format("(MatrixModifiedDT=%s+)",
                                syncDateTime.format(DateTimeFormatter.ISO_DATE_TIME)))
                        .setDeleteQuery(String.format("(MatrixModifiedDT=%s-)",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
                        .build());
    }

    public SyncStat stashSearchTerms() {

        logger.debug("Calling stash search term");

        long syncStartTime = System.currentTimeMillis();

        Iterable<ListingResidential> listings = repository.findAll();

        searchTermResidentialRepository.deleteAll();
        searchTermSchoolRepository.deleteAll();

        SortedSet<SearchTermResidential> resiTerms2Save = new TreeSet<>();
        SortedSet<SearchTermSchool> schoolTerms2Save = new TreeSet<>();

        for(ListingResidential listing : listings) {
            //street address
            String streetAddress = buildStreetAddr(listing);

            if (StringUtils.isNotEmpty(streetAddress)) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(streetAddress);
                searchTerm.setType(STREET);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //city
            String city = listing.getCity();

            if (StringUtils.isNotEmpty(city) && city.length() > 2) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(city);
                searchTerm.setType(CITY);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //zipcode
            String zipcode = listing.getPostalCode();

            if (StringUtils.isNotEmpty(zipcode) && zipcode.length() > 2) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(zipcode);
                searchTerm.setType(ZIPCODE);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //neighbourhood
            String neighbourhood = listing.getNeighbourhood();

            if (StringUtils.isNotEmpty(neighbourhood) && neighbourhood.length() > 2) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(neighbourhood);
                searchTerm.setType(NEIGHBOURHOOD);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //building name
            String buildingName = listing.getBuildingName();

            if (StringUtils.isNotEmpty(buildingName) && buildingName.length() > 2) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(buildingName);
                searchTerm.setType(BUILDING_NAME);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //mls number
            String mlsNumber = listing.getMlsNumber();

            if (StringUtils.isNotEmpty(mlsNumber) && mlsNumber.length() > 2) {
                SearchTermResidential searchTerm = new SearchTermResidential();
                searchTerm.setSearchTerm(mlsNumber);
                searchTerm.setType(MLS_NUMBER);
                if (!resiTerms2Save.contains(searchTerm)) {
                    resiTerms2Save.add(searchTerm);
                }
            }

            //elementary school
            String elementarySchool = listing.getElementarySchool();

            if (StringUtils.isNotEmpty(elementarySchool) && elementarySchool.length() > 2) {
                SearchTermSchool searchTerm = new SearchTermSchool();
                searchTerm.setSearchTerm(elementarySchool);
                searchTerm.setType(ELEMENTARY_SCHOOL);
                if (!schoolTerms2Save.contains(searchTerm)) {
                    schoolTerms2Save.add(searchTerm);
                }
            }

            //middle school
            String middleSchool = listing.getMiddleOrJuniorSchool();

            if (StringUtils.isNotEmpty(middleSchool) && middleSchool.length() > 2) {
                SearchTermSchool searchTerm = new SearchTermSchool();
                searchTerm.setSearchTerm(middleSchool);
                searchTerm.setType(MIDDLE_SCHOOL);
                if (!schoolTerms2Save.contains(searchTerm)) {
                    schoolTerms2Save.add(searchTerm);
                }
            }

            //high school
            String highSchool = listing.getHighSchool();

            if (StringUtils.isNotEmpty(highSchool) && highSchool.length() > 2) {
                SearchTermSchool searchTerm = new SearchTermSchool();
                searchTerm.setSearchTerm(highSchool);
                searchTerm.setType(HIGH_SCHOOL);
                if (!schoolTerms2Save.contains(searchTerm)) {
                    schoolTerms2Save.add(searchTerm);
                }
            }
        }

        searchTermResidentialRepository.save(resiTerms2Save);
        searchTermSchoolRepository.save(schoolTerms2Save);

        long syncEndTime = System.currentTimeMillis();

        SyncStat syncStat = new SyncStatBuilder()
                .setNumNewRecords(resiTerms2Save.size() + schoolTerms2Save.size())
                .setSyncType(SearchTerm.class.getName())
                .setSyncStartTime(new Date(syncStartTime))
                .setSyncEndTime(new Date(syncEndTime))
                .setStatus(COMPLETE)
                .build();

        syncStat.beforeSave();

        syncStatRepository.save(syncStat);

        return syncStat;
    }

    private String buildStreetAddr(ListingResidential listing) {
        StringBuilder streetAddr = new StringBuilder();

        if (StringUtils.isNotEmpty(listing.getStreetNumber())) {
            streetAddr.append(listing.getStreetNumber());
        }

        if (StringUtils.isNotEmpty(listing.getStreetDirPrefix())) {
            streetAddr.append(" " + listing.getStreetDirPrefix());
        }

        if (StringUtils.isNotEmpty(listing.getStreetName())) {
            streetAddr.append(" " + listing.getStreetName());
        }

        if (StringUtils.isNotEmpty(listing.getStreetDirSuffix())) {
            streetAddr.append(" " + listing.getStreetDirSuffix());
        }

        if (StringUtils.isNotEmpty(listing.getStreetSuffix())) {
            streetAddr.append(" " + listing.getStreetSuffix());
        }

        if (StringUtils.isNotEmpty(listing.getUnitNumber())) {
            streetAddr.append(", " + listing.getUnitNumber());
        }

        return streetAddr.toString();
    }
}
