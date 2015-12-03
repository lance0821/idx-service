package idxsync.sync.service;


public class EmailNotificationConfig {

    private String notificationEmailAddress;
    private boolean excludeStatusComplete;

    public EmailNotificationConfig(String notificationEmailAddress, boolean excludeStatusComplete) {
        this.notificationEmailAddress = notificationEmailAddress;
        this.excludeStatusComplete = excludeStatusComplete;
    }

    public String getNotificationEmailAddress() {
        return notificationEmailAddress;
    }

    public void setNotificationEmailAddress(String notificationEmailAddress) {
        this.notificationEmailAddress = notificationEmailAddress;
    }

    public boolean isExcludeStatusComplete() {
        return excludeStatusComplete;
    }

    public void setExcludeStatusComplete(boolean excludeStatusComplete) {
        this.excludeStatusComplete = excludeStatusComplete;
    }
}
