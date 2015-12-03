package idxsync.sync.controller;

public class SyncStartResponse {

    private String syncToken;

    public String getSyncToken() {
        return syncToken;
    }

    public void setSyncToken(String syncToken) {
        this.syncToken = syncToken;
    }
}
