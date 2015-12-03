package idxsync.domain;


import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

import static idxsync.domain.SyncStatus.ERROR;

@Document(indexName = "idx", type = "sync_stat")
public class SyncStat extends AbstractSyncStat {

    private String syncType;
    private Integer numRecordsFromRets;
    private Integer numRecordsInDbPreSync;
    private Integer numRecordsInDbPostSync;
    private Integer numNewRecords;
    private Integer numUpdatedRecords;
    private Integer numDeletedRecords;
    
    public SyncStat() {
        super();

        init("");
    }

    public SyncStat(String syncType) {
        super();

        init(syncType);
    }

    private void init(String syncType) {
        this.syncType = syncType;

        this.numRecordsFromRets = 0;
        this.numRecordsInDbPreSync = 0;
        this.numRecordsInDbPostSync = 0;
        this.numNewRecords = 0;
        this.numUpdatedRecords = 0;
        this.numDeletedRecords = 0;

        this.syncDurationMillis = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("SyncType: %s\n", syncType));
        sb.append(String.format("Sync duration (milliseconds): %d\n", syncDurationMillis));
        sb.append(String.format("Sync Success: %b\n", status));

        if (status == ERROR) {
            sb.append(String.format("Error Message: %s\n", errorMessage));
            return sb.toString();
        }

        sb.append(String.format("numRecordsFromRets: %d\n", numRecordsFromRets));
        sb.append(String.format("numRecordsInDbPreSync: %d\n", numRecordsInDbPreSync));
        sb.append(String.format("numRecordsInDbPostSync: %d\n", numRecordsInDbPostSync));
        sb.append(String.format("numNewRecords: %d\n", numNewRecords));
        sb.append(String.format("numUpdatedRecords: %d\n", numUpdatedRecords));
        sb.append(String.format("numDeletedRecords: %d\n", numDeletedRecords));

        return sb.toString();
    }

    public void incrementNumRecordsFromRets() {
        this.numRecordsFromRets++;
    }

    public void incrementNumRecordsInDbPreSync() {
        this.numRecordsInDbPreSync++;
    }

    public void incrementNumNewRecords() {
        this.numNewRecords++;
    }

    public void incrementNumUpdatedRecords() {
        this.numUpdatedRecords++;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public Integer getNumRecordsFromRets() {
        return numRecordsFromRets;
    }

    public void setNumRecordsFromRets(Integer numRecordsFromRets) {
        this.numRecordsFromRets = numRecordsFromRets;
    }

    public Integer getNumRecordsInDbPreSync() {
        return numRecordsInDbPreSync;
    }

    public void setNumRecordsInDbPreSync(Integer numRecordsInDbPreSync) {
        this.numRecordsInDbPreSync = numRecordsInDbPreSync;
    }

    public Integer getNumRecordsInDbPostSync() {
        return numRecordsInDbPostSync;
    }

    public void setNumRecordsInDbPostSync(Integer numRecordsInDbPostSync) {
        this.numRecordsInDbPostSync = numRecordsInDbPostSync;
    }

    public Integer getNumNewRecords() {
        return numNewRecords;
    }

    public void setNumNewRecords(Integer numNewRecords) {
        this.numNewRecords = numNewRecords;
    }

    public Integer getNumUpdatedRecords() {
        return numUpdatedRecords;
    }

    public void setNumUpdatedRecords(Integer numUpdatedRecords) {
        this.numUpdatedRecords = numUpdatedRecords;
    }

    public Integer getNumDeletedRecords() {
        return numDeletedRecords;
    }

    public void setNumDeletedRecords(Integer numDeletedRecords) {
        this.numDeletedRecords = numDeletedRecords;
    }

    public static class SyncStatBuilder {

        private String syncType;
        private SyncStatus status;
        private String errorMessage;
        private Integer numRecordsFromRets;
        private Integer numRecordsInDbPreSync;
        private Integer numRecordsInDbPostSync;
        private Integer numNewRecords;
        private Integer numUpdatedRecords;
        private Integer numDeletedRecords;
        private Date syncStartTime;
        private Date syncEndTime;

        public SyncStatBuilder setSyncType(String syncType) {
            this.syncType = syncType;
            return this;
        }

        public SyncStatBuilder setStatus(SyncStatus status) {
            this.status = status;
            return this;
        }

        public SyncStatBuilder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public SyncStatBuilder setNumRecordsFromRets(Integer numRecordsFromRets) {
            this.numRecordsFromRets = numRecordsFromRets;
            return this;
        }

        public SyncStatBuilder setNumRecordsInDbPreSync(Integer numRecordsInDbPreSync) {
            this.numRecordsInDbPreSync = numRecordsInDbPreSync;
            return this;
        }

        public SyncStatBuilder setNumRecordsInDbPostSync(Integer numRecordsInDbPostSync) {
            this.numRecordsInDbPostSync = numRecordsInDbPostSync;
            return this;
        }

        public SyncStatBuilder setNumNewRecords(Integer numNewRecords) {
            this.numNewRecords = numNewRecords;
            return this;
        }

        public SyncStatBuilder setNumUpdatedRecords(Integer numUpdatedRecords) {
            this.numUpdatedRecords = numUpdatedRecords;
            return this;
        }

        public SyncStatBuilder setNumDeletedRecords(Integer numDeletedRecords) {
            this.numDeletedRecords = numDeletedRecords;
            return this;
        }

        public SyncStatBuilder setSyncStartTime(Date syncStartTime) {
            this.syncStartTime = syncStartTime;
            return this;
        }

        public SyncStatBuilder setSyncEndTime(Date syncEndTime) {
            this.syncEndTime = syncEndTime;
            return this;
        }

        public SyncStat build() {
            SyncStat syncStat = new SyncStat();

            syncStat.setSyncType(syncType);
            syncStat.setStatus(status);
            syncStat.setErrorMessage(errorMessage);
            syncStat.setNumRecordsFromRets(numRecordsFromRets);
            syncStat.setNumRecordsInDbPreSync(numRecordsInDbPreSync);
            syncStat.setNumRecordsInDbPostSync(numRecordsInDbPostSync);
            syncStat.setNumNewRecords(numNewRecords);
            syncStat.setNumUpdatedRecords(numUpdatedRecords);
            syncStat.setNumDeletedRecords(numDeletedRecords);
            syncStat.setSyncStartTime(syncStartTime);
            syncStat.setSyncEndTime(syncEndTime);

            return syncStat;
        }
    }
}
