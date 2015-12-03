package idxsync.sync.controller;


import idxsync.domain.SyncFullStat;

public class SyncStatusResponse {

    private SyncFullStat syncFullStat;

    public SyncFullStat getSyncFullStat() {
        return syncFullStat;
    }

    public void setSyncFullStat(SyncFullStat syncFullStat) {
        this.syncFullStat = syncFullStat;
    }
}
