package idxsync.sync.service;

import idxsync.domain.SyncFullStat;
import idxsync.domain.SyncStat;

import java.time.LocalDateTime;

public interface SyncServiceFacade {

    SyncFullStat syncIdxData(String syncToken);
    SyncFullStat syncIdxData(String syncToken, LocalDateTime initialSyncDate);
}
