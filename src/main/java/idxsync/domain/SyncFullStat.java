package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.NestedField;

import java.util.LinkedHashSet;
import java.util.Set;

import static idxsync.domain.SyncStatus.ERROR;

@Document(indexName = "idx", type = "sync_stat_full")
public class SyncFullStat extends AbstractSyncStat {
    
    private String syncToken;

    @NestedField(dotSuffix = "idxsync.domain", type = FieldType.Object)
    private Set<SyncStat> syncStatList;

    public Set<SyncStat> getSyncStatList() {

        if (syncStatList == null) {
            syncStatList = new LinkedHashSet<>();
        }

        return syncStatList;
    }

    public String getSyncToken() {
        return syncToken;
    }

    public void setSyncToken(String syncToken) {
        this.syncToken = syncToken;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Sync Token: %s\n", syncToken));
        sb.append(String.format("Sync duration (milliseconds): %d\n", syncDurationMillis));
        sb.append(String.format("Sync Success: %b\n", status));

        if (status == ERROR) {
            sb.append(String.format("Error Message: %s\n", errorMessage));
            return sb.toString();
        }

        sb.append(String.format("Number of sync stats: %d", getSyncStatList().size()));

        return sb.toString();
    }
}
