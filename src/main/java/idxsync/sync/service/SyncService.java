package idxsync.sync.service;


import idxsync.domain.SyncStat;
import idxsync.rets.RetsConnection;

import java.time.LocalDateTime;

public interface SyncService {

    SyncStat sync(RetsConnection retsConnection, LocalDateTime syncDateTime);
}
