package idxsync.sync.service;

import idxsync.domain.ListingLand;
import idxsync.domain.SyncStat;
import idxsync.mapping.ListingLandMapper;
import idxsync.persistence.repository.ListingLandRepository;
import idxsync.rets.RetsConnection;
import idxsync.sync.strategy.SyncStrategy;
import idxsync.sync.strategy.SyncStrategyConfig.SyncConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SyncListingLandService implements SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncListingLandService.class);

    @Resource
    private ListingLandRepository repository;

    @Autowired
    private ListingLandMapper mapper;

    @Autowired
    @Qualifier("syncStrategyIdx")
    private SyncStrategy syncStrategy;

    @Override
    public SyncStat sync(RetsConnection retsConnection, LocalDateTime syncDateTime) {
        logger.debug("calling sync listing land");
        return syncStrategy.sync(
                new SyncConfigBuilder()
                        .setDomainName(ListingLand.class.getName())
                        .setRetsConnection(retsConnection)
                        .setMapper(mapper)
                        .setDomainRepository(repository)
                        .setSyncQuery(String.format("(MatrixModifiedDT=%s+)",
                                syncDateTime.format(DateTimeFormatter.ISO_DATE_TIME)))
                        .setDeleteQuery(String.format("(MatrixModifiedDT=%s-)",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)))
                        .build());
    }
}
