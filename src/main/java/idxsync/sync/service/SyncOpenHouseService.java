package idxsync.sync.service;

import idxsync.domain.OpenHouse;
import idxsync.domain.SyncStat;
import idxsync.mapping.OpenHouseMapper;
import idxsync.persistence.repository.OpenHouseRepository;
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

@Service
public class SyncOpenHouseService implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncOpenHouseService.class);

    @Resource
    private OpenHouseRepository repository;

    @Autowired
    private OpenHouseMapper mapper;

    @Autowired
    @Qualifier("syncStrategyIdx")
    private SyncStrategy syncStrategy;

    @Override
    public SyncStat sync(RetsConnection retsConnection, LocalDateTime syncDateTime) {
        logger.debug("Calling sync open house");
        return syncStrategy.sync(
                new SyncConfigBuilder()
                        .setDomainName(OpenHouse.class.getName())
                        .setSyncQuery("(ActiveYN=1)")
                        .setDeleteQuery("(ActiveYN=1)")
                        .setRetsConnection(retsConnection)
                        .setMapper(mapper)
                        .setDomainRepository(repository)
                        .build());
    }

}
