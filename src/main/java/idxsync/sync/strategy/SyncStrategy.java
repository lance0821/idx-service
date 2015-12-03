package idxsync.sync.strategy;

import idxsync.domain.SyncStat;

public interface SyncStrategy {

    SyncStat sync(SyncStrategyConfig syncStrategyConfig);
}
